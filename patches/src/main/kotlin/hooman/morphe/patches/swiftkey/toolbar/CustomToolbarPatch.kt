package hooman.morphe.patches.swiftkey.toolbar

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.instructions
import app.morphe.patcher.extensions.InstructionExtensions.removeInstructions
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.resourcePatch
import app.morphe.patcher.util.addFreeRegisters
import app.morphe.patcher.util.p0Register
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import hooman.morphe.patches.swiftkey.swiftKeyCompatibility
import org.w3c.dom.Element

private const val TOOLBAR = "Lcom/touchtype/keyboard/toolbar/Toolbar;"
private const val BRIDGE = "Lapp/morphe/extension/swiftkey/ToolbarBridge;"
private const val HOOKS = "Lapp/morphe/extension/swiftkey/ToolbarHooks;"
private const val ACTIVITY = "app.morphe.extension.swiftkey.ToolbarSettingsActivity"

private object ToolbarRenderFingerprint : Fingerprint(
    definingClass = TOOLBAR, name = "g", returnType = "V", parameters = listOf("I", "Ljava/lang/Object;")
)
private object ToolbarStateFingerprint : Fingerprint(
    name = "toString", returnType = "Ljava/lang/String;", parameters = emptyList(),
    strings = listOf("ToolbarItemState(dynamicToolbarItems=")
)

private val customToolbarResources = resourcePatch {
    compatibleWith(swiftKeyCompatibility)
    execute {
        document("res/values/strings.xml").use { doc ->
            val root = doc.documentElement
            for ((name, value) in mapOf("clean_toolbar_title" to "Personalizar toolbar",
                "clean_toolbar_summary" to "Escolha os atalhos, a quantidade e a ordem da barra")) {
                if ((0 until root.childNodes.length).any {
                    (root.childNodes.item(it) as? Element)?.getAttribute("name") == name
                }) throw PatchException("Toolbar already patched: $name")
                root.appendChild(doc.createElement("string").apply { setAttribute("name", name); textContent = value })
            }
        }
        document("AndroidManifest.xml").use { doc ->
            val apps = doc.getElementsByTagName("application")
            if (apps.length != 1) throw PatchException("Expected one SwiftKey application")
            apps.item(0).appendChild(doc.createElement("activity").apply {
                setAttribute("android:name", ACTIVITY)
                setAttribute("android:exported", "false")
                setAttribute("android:label", "@string/clean_toolbar_title")
                setAttribute("android:theme", "@android:style/Theme.Material.NoActionBar")
                setAttribute("android:parentActivityName", "com.touchtype.materialsettingsx.NavigationActivity")
            })
        }
        document("res/xml/prefsx_home_screen.xml").use { doc ->
            val preference = doc.createElement("Preference").apply {
                setAttribute("android:key", "swiftkey_clean_toolbar_editor")
                setAttribute("android:title", "@string/clean_toolbar_title")
                setAttribute("android:summary", "@string/clean_toolbar_summary")
                setAttribute("android:icon", "@drawable/ic_home_layout")
                setAttribute("android:persistent", "false")
                appendChild(doc.createElement("intent").apply {
                    setAttribute("android:targetPackage", "com.touchtype.swiftkey")
                    setAttribute("android:targetClass", ACTIVITY)
                })
            }
            val root = doc.documentElement
            val anchor = (0 until root.childNodes.length).mapNotNull { root.childNodes.item(it) as? Element }
                .singleOrNull { it.getAttribute("android:key") == "@string/pref_home_launch_layout_and_keys_prefs" }
                ?: throw PatchException("SwiftKey layout/settings preference changed")
            root.insertBefore(preference, anchor.nextSibling)
        }
    }
}

@Suppress("unused")
val customToolbarPatch = bytecodePatch(
    name = "Customise toolbar",
    description = "Adds a local toolbar editor to SwiftKey settings. Choose and reorder shortcuts; " +
        "native layout distributes the visible icons and More options remains available.",
) {
    compatibleWith(swiftKeyCompatibility)
    dependsOn(customToolbarResources)
    extendWith("extensions/swiftkey.mpe")
    execute {
        val state = ToolbarStateFingerprint.method.definingClass
        val stateClass = classDefBy(state)
        val expectedFields = mapOf("a" to "Ljava/util/List;", "b" to "Ljava/util/ArrayList;",
            "c" to "Ljava/util/List;", "d" to "I")
        if (!expectedFields.all { (name, type) -> stateClass.fields.count { it.name == name && it.type == type } == 1 })
            throw PatchException("SwiftKey toolbar state fields changed")
        val ctor = stateClass.methods.singleOrNull {
            it.name == "<init>" && it.parameterTypes.map(CharSequence::toString) ==
                listOf("Ljava/util/List;", "Ljava/util/ArrayList;", "Ljava/util/List;", "I")
        } ?: throw PatchException("SwiftKey toolbar state constructor changed")
        val originalRender = ToolbarRenderFingerprint.method
        val getter = originalRender.instructions.mapNotNull {
            (it as? ReferenceInstruction)?.reference as? MethodReference
        }.singleOrNull { it.returnType == state && it.parameterTypes.isEmpty() }
            ?: throw PatchException("SwiftKey toolbar model getter was not unique")
        val modelField = classDefBy(TOOLBAR).fields.singleOrNull { it.type == getter.definingClass }
            ?: throw PatchException("SwiftKey toolbar model field was not unique")
        val itemInterface = originalRender.instructions.mapNotNull {
            (it as? ReferenceInstruction)?.reference as? MethodReference
        }.map { it.definingClass }.distinct().mapNotNull { classDefByOrNull(it) }
            .singleOrNull { candidate ->
                listOf("getItemId" to "I", "g" to "Ljava/lang/String;", "d" to "I").all { (name, result) ->
                    candidate.methods.count { it.name == name && it.returnType == result && it.parameterTypes.isEmpty() } == 1
                }
            }?.type ?: throw PatchException("SwiftKey toolbar item interface was not unique")

        val bridge = mutableClassDefBy(BRIDGE)
        fun rewrite(name: String, body: String) {
            val method = bridge.methods.single { it.name == name }.addFreeRegisters(6).first
            method.removeInstructions(0, method.instructions.size)
            method.addInstructions(0, body)
        }
        for ((name, hostName, result) in listOf(Triple("itemId", "getItemId", "I"),
            Triple("caption", "g", "Ljava/lang/String;"), Triple("icon", "d", "I"))) {
            val objectResult = result.startsWith("L")
            rewrite(name, """
                check-cast p0, $itemInterface
                invoke-interface {p0}, $itemInterface->$hostName()$result
                move-result${if (objectResult) "-object" else ""} v0
                return${if (objectResult) "-object" else ""} v0
            """)
        }
        rewrite("transform", """
            check-cast p0, $state
            iget-object v0, p0, $state->a:Ljava/util/List;
            iget-object v1, p0, $state->b:Ljava/util/ArrayList;
            iget-object v2, p0, $state->c:Ljava/util/List;
            invoke-static {v0, v1, v2, p1}, $HOOKS->select(Ljava/util/List;Ljava/util/List;Ljava/util/List;Landroid/view/View;)Ljava/util/ArrayList;
            move-result-object v0
            new-instance v1, $state
            new-instance v2, Ljava/util/ArrayList;
            invoke-direct {v2}, Ljava/util/ArrayList;-><init>()V
            iget-object v3, p0, $state->c:Ljava/util/List;
            iget v4, p0, $state->d:I
            invoke-direct {v1, v0, v2, v3, v4}, $ctor
            return-object v1
        """)
        rewrite("refresh", """
            check-cast p0, $TOOLBAR
            iget-object v0, p0, $modelField
            invoke-virtual {v0}, $getter
            move-result-object v0
            const/4 v1, 0x0
            invoke-virtual {p0, v1, v0}, $TOOLBAR->g(ILjava/lang/Object;)V
            return-void
        """)

        val scratch = originalRender.implementation!!.registerCount
        // addFreeRegisters copies incoming parameters back to their original
        // absolute registers. A p0 alias can then overlap a copied parameter.
        val originalThis = originalRender.p0Register
        val render = originalRender.addFreeRegisters(2).first
        val index = render.instructions.indexOfFirst {
            ((it as? ReferenceInstruction)?.reference as? MethodReference)?.toString() == getter.toString()
        }
        val result = render.instructions.getOrNull(index + 1)
        if (index < 0 || result?.opcode != Opcode.MOVE_RESULT_OBJECT)
            throw PatchException("SwiftKey toolbar getter result changed")
        val register = (result as OneRegisterInstruction).registerA
        render.addInstructions(index + 2, """
            move-object/from16 v$scratch, v$register
            move-object/from16 v${scratch + 1}, v$originalThis
            invoke-static/range {v$scratch .. v${scratch + 1}}, $BRIDGE->transform(Ljava/lang/Object;Landroid/view/View;)Ljava/lang/Object;
            move-result-object v$register
            check-cast v$register, $state
        """)
    }
}
