# Restore Arabic 123 legacy layout

Select **Restore Arabic 123 legacy layout** in Morphe when patching an original
SwiftKey **9.13.19.3** APK. After installation, open **SwiftKey → Languages**,
select your enabled Arabic language and choose **Arabic 123 - Legacy** in the
layout chooser. The label is localized by SwiftKey. Restart SwiftKey if an
already-open chooser has not refreshed.

The patch adds a choice; it does not automatically switch your layout. It can be
selected independently of the clipboard, toolbar and privacy patches. Apply all
desired patches in one pass to an original APK.

## What was found

The original APK contains the `ARABIC_WESTERN` enum entry (`arabic_western`),
the English label **Arabic 123 - Legacy**, its primary-key definition, and the
standard, compact and PC key maps. The language manager still accepts this
layout as a saved selection. No replacement layout assets are needed.

Microsoft's Arabic model archives referenced by this APK were inspected on
2026-11-11, with their SHA-1 values checked against the bundled catalog. All four
Arabic packs (`ar_SA`, `ar_MA`, `ar_TN`, `ar_DZ`) offer `arabic_western_v2` in
`ime.json` but omit `arabic_western`. This explains the missing choice for these
packs; it does not establish why Microsoft removed it from their configuration.

## Implementation

`Luj0/f;->a()Ljava/util/List;` constructs the available layout list, puts the
pack's default first, and wraps the result as unmodifiable. Immediately before
that wrapper, the patch adds `ARABIC_WESTERN` if `ARABIC_WESTERN_V2` is present
and the legacy layout is absent. The existing list order and default are kept.
The underlying language-pack data is not modified. Consequently fresh lists
after a pack update receive the same addition if Arabic 123 remains available.

The patch validates the exact original register flow, instruction references,
method signature and enum strings before editing. Already-patched and changed
methods are rejected. Branches use external labels attached to the original
immutable-list instruction, so their targets remain correct after insertion.

Native layout rendering, long-press keys, symbols, preview, selection and saving
remain SwiftKey's own code. This restores the western-number **123** legacy
layout only; it does not add the separate **١٢٣** legacy variant.

## Validation

The patch was applied alone and with all four other patches to Microsoft's
original ARM64 APK. Both outputs rebuilt and signed. For each output,
`VerifyArabicLayoutPatch.java` passed **295 behavior assertions** against the
actual emitted DEX and **111 structural/resource checks**. Cases include all four
Arabic packs, modern and legacy defaults, an existing legacy choice, empty lists,
other languages, repeated reads, unchanged input data and immutable output.
The original key maps, layout enum and language-manager methods were preserved.

These are limited DEX execution-model tests, not Android/ART execution. Physical
device tests are still required for the chooser preview, actual Arabic input,
long-press characters, symbols, layout variants and selection after restart.

To run the verifier after patching:

```sh
mkdir -p artifacts/verification-classes
javac -cp tools/morphe-desktop-2.1.1-all.jar -d artifacts/verification-classes \
  scripts/VerifyClipboardPatch.java scripts/VerifyArabicLayoutPatch.java
java -cp tools/morphe-desktop-2.1.1-all.jar:artifacts/verification-classes \
  VerifyArabicLayoutPatch /path/to/original.apk /path/to/patched.apk
```
