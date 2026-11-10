# M1zir’s SwiftKey Clipboard Patches v0.2.0 (experimental)

Maintained by **Abubakr Ahmed (M1zir)**. Targets **Microsoft SwiftKey 9.13.19.3**
(`com.touchtype.swiftkey`).

## New: Keep clipboard history forever

Removes the one-hour expiration for local clipboard entries and fixes timestamp
sorting for entries weeks apart. Screenshots and images that SwiftKey successfully
adds to its clipboard use the same expiration rule. Pinning, manual deletion,
and normal save/load behavior are preserved.

No fixed local entry-count cap was found in the inspected storage paths. Device
storage and memory still set practical limits. Expired clips cannot be recovered;
cloud clipboard behavior is unchanged.

## Install

[Add source to Morphe](https://morphe.software/add-source?github=M1zir/swiftkey-clipboard-patches)

Enable pre-releases for this experimental source if needed. Alternatively, download
`swiftkey-clipboard-patches-0.2.0.mpp` below and import it through
**Sources → Add → Local**. Patch an **original SwiftKey 9.13.19.3 APK**, select
**Keep clipboard history forever**, and enable **Remember copied items** in SwiftKey.
Deselect the other patches if only clipboard retention is wanted.

Android may refuse installation over the official app because signatures differ.
Save important clips/settings before any uninstall, which erases local app data.

## Validation

Built and applied using Morphe Desktop 2.1.1 / Patcher 2.3.0 to Microsoft's original
ARM64 APK, both alone and with all three retained upstream patches. Strict
fingerprint checks passed, the APKs rebuilt and signed, and 394 emitted-bytecode
behavior assertions passed on each output. Repatching an already modified APK is
rejected. Six upstream JVM tests passed; eight toolbar UI tests could not run
because their Robolectric native runtime does not support the Linux ARM64 host.

**Android Manager import, actual device retention (including screenshots), and
large-history performance remain untested.** See VALIDATION.md for details.

## Attribution

Based on murilosimao/swiftkey-patch v0.1.1, with the optional toolbar and privacy
patches retained. Upstream credit to Murilo and arandomhooman is preserved.
GPL-3.0; this is an independent custom fork, not an official upstream release.
No SwiftKey APKs, decompiled application code, or signing keys are distributed.
