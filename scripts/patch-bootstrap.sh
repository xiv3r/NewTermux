#!/bin/bash
set -e

ARCH=$1
OLD_ID="com.termux"
NEW_ID="com.newtermux.app"
OLD_PREFIX="/data/data/com.termux/files/usr"
NEW_PREFIX="/data/data/com.newtermux.app/files/usr"

mkdir -p extract
unzip -q "bootstrap-${ARCH}.zip" -d extract/
cd extract

echo "1. Patching text files and shebangs for ${ARCH}..."
# Fix shebangs and other text occurrences
# We search for the old package ID in all files that look like text/scripts
grep -rIl "$OLD_ID" . | xargs sed -i "s|$OLD_ID|$NEW_ID|g" || true

echo "2. Patching ELF binaries for ${ARCH}..."
# Find all ELF files
find . -type f | while read f; do
    if file "$f" | grep -q 'ELF'; then
        # Update RPATH
        OLD_RPATH=$(patchelf --print-rpath "$f" 2>/dev/null || true)
        if [ -n "$OLD_RPATH" ]; then
            NEW_RPATH=$(echo "$OLD_RPATH" | sed "s|$OLD_ID|$NEW_ID|g")
            patchelf --set-rpath "$NEW_RPATH" "$f" 2>/dev/null || true
        fi
        
        # Update Interpreter (dynamic linker path)
        OLD_INTERP=$(patchelf --print-interpreter "$f" 2>/dev/null || true)
        if [ -n "$OLD_INTERP" ] && echo "$OLD_INTERP" | grep -q "$OLD_ID"; then
            NEW_INTERP=$(echo "$OLD_INTERP" | sed "s|$OLD_ID|$NEW_ID|g")
            patchelf --set-interpreter "$NEW_INTERP" "$f" 2>/dev/null || true
        fi
    fi
done

echo "3. Updating SYMLINKS.txt for ${ARCH}..."
if [ -f SYMLINKS.txt ]; then
    sed -i "s|$OLD_ID|$NEW_ID|g" SYMLINKS.txt
fi

echo "4. MOTD update for ${ARCH}..."
if [ -f etc/motd ]; then
    echo "Welcome to NewTermux!" > etc/motd
fi

echo "5. Repacking ${ARCH}..."
zip -q -r "../bootstrap-${ARCH}.zip" .
cd ..
rm -rf extract
echo "Done! Patched bootstrap-${ARCH}.zip for NewTermux."
