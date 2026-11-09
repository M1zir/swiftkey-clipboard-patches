# Attribution

The SwiftKey privacy and cloud sign-in patches are adapted from
[arandomhooman/hoomans-morphe-patches](https://github.com/arandomhooman/hoomans-morphe-patches),
commit `018277825a611ad61f3a16a70e562291c06f4081` (GPL-3.0).
Their author is arandomhooman. The original target is SwiftKey 9.13.13.5;
this project adapts them to 9.13.19.3 and adds a toolbar editor.

Morphe Desktop/Patcher is the patch engine, developed by the Morphe project,
based in part on prior ReVanced work. This project is independent and is not
an official Morphe or Microsoft product.

The Gradle wrapper is copied from the upstream patches project. Gradle is
distributed under the Apache License 2.0. Google R8/D8 is also Apache-2.0.
Neither Desktop nor R8 is committed here: the bootstrap script downloads
public releases and checks their SHA-256 hashes.

SwiftKey is Microsoft's application. Its APKs, split APKs, decompiled source,
native libraries, signing keys and patched application are not distributed
in this repository or its releases.
