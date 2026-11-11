# M1zir’s SwiftKey Clipboard Patches v0.3.0 (experimental)

Maintained by **Abubakr Ahmed (M1zir)**. Targets **Microsoft SwiftKey 9.13.19.3**
(`com.touchtype.swiftkey`).

## New: Restore Arabic 123 legacy layout

Adds **Arabic 123 - Legacy** back to the Arabic layout chooser using the original
layout still bundled in SwiftKey. The current layouts and default selection stay
intact. No old APK or separate layout download is required.

Enable **Restore Arabic 123 legacy layout** when patching, then open
**SwiftKey → Languages → your Arabic language** and choose **Arabic 123 - Legacy**.
The patch is independently selectable and can be combined with clipboard retention.

## Included patches

- Restore Arabic 123 legacy layout
- Keep clipboard history forever
- Customise toolbar
- Disable cloud sign-in prompt
- Disable telemetry

Clipboard retention still removes local one-hour expiration, including images
saved to SwiftKey’s clipboard, and fixes sorting for entries weeks apart.
Manual deletion and pinning are preserved. Storage and memory set practical
limits; expired clips cannot be recovered and cloud behavior is unchanged.

## Install or update

[Add source to Morphe](https://morphe.software/add-source?github=M1zir/swiftkey-clipboard-patches)

Enable pre-releases for this experimental source if needed, then refresh it.
Alternatively download `swiftkey-clipboard-patches-0.3.0.mpp` below and import it
through **Sources → Add → Local**. Apply all desired patches in one pass to an
**original, unpatched SwiftKey 9.13.19.3 APK**, including when updating from v0.2.0.
For clipboard retention, enable **Remember copied items** in SwiftKey.

Use the same Morphe signing key as your previous patched installation to permit
an in-place update. The official Microsoft installation has a different signature;
Android may refuse to install over it. Save important data before any uninstall.

## Validation

Applied the layout patch alone and all five patches together to Microsoft’s
original ARM64 APK using Morphe Desktop 2.1.1 / Patcher 2.3.0. Both APKs rebuilt
and signed. Each passed 295 layout behavior assertions and 111 structural/resource
checks on the emitted DEX. The combined output also passed 394 clipboard behavior
assertions and the upstream toolbar/telemetry verifier. Repatching is rejected.

**These are bytecode execution-model checks. Android Manager import, actual
Arabic typing/layout previews, clipboard retention and large-history performance
still require physical-device testing.** See VALIDATION.md and docs/ARABIC_LAYOUT.md.

## Attribution

Independent GPL-3.0 fork of murilosimao/swiftkey-patch v0.1.1, preserving credit
to Murilo and arandomhooman. Not an official Microsoft, Morphe or upstream release.
No SwiftKey APKs, original layout assets, decompiled app code or signing keys are
distributed.
