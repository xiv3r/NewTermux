#!/bin/bash
set -e

ARCH=$1
OLD_USR="/data/data/com.termux/files/usr"
NEW_USR="/data/data/com.newtermux.app/u"
OLD_HOME="/data/data/com.termux/files/home"
NEW_HOME="/data/data/com.newtermux.app//h"

mkdir -p extract
unzip -q "bootstrap-${ARCH}.zip" -d extract/
cd extract

# 1. Text patch (scripts, configs)
# sed is safe for text files even if length changes, but we use the same paths for consistency.
echo "Patching scripts and configs..."
find . -type f | while read f; do
  if file "$f" | grep -qE 'text|script'; then
    sed -i "s|${OLD_USR}|${NEW_USR}|g" "$f" 2>/dev/null || true
    sed -i "s|${OLD_HOME}|${NEW_HOME}|g" "$f" 2>/dev/null || true
    sed -i "s|com.termux|com.newtermux.app|g" "$f" 2>/dev/null || true
    sed -i "s|Termux|NewTermux|g" "$f" 2>/dev/null || true
  fi
done

# 2. Binary patch using fixed-length replacement
echo "Patching binaries safely (Fixed-Length Strategy)..."
python3 ../scripts/patch-binary.py

# 3. ELF header patch
# Double-check important headers with patchelf.
echo "Verifying ELF headers..."
find . -type f | while read f; do
  if file "$f" | grep -q 'ELF'; then
    # The binary patcher already updated these strings, but let's ensure patchelf sees them.
    # Note: patchelf is used here mainly for verification or fixing if binary patch missed something.
    INTERP=$(patchelf --print-interpreter "$f" 2>/dev/null || true)
    if [ -n "$INTERP" ] && echo "$INTERP" | grep -qF "com.termux"; then
       # Fallback if python script missed it
       NEW_INTERP=$(echo "$INTERP" | sed "s|${OLD_USR}|${NEW_USR}|g")
       patchelf --set-interpreter "$NEW_INTERP" "$f" 2>/dev/null || true
    fi
  fi
done

# 4. MOTD fix
if [ -f etc/motd ]; then
  echo "Welcome to NewTermux!" > etc/motd
fi

# 5. Permissions
find bin libexec -type f -exec chmod +x {} + 2>/dev/null || true
find usr/bin usr/libexec -type f -exec chmod +x {} + 2>/dev/null || true

# 6. Repack
zip -q -r "../bootstrap-${ARCH}.zip" .
cd ..
echo "Repacked bootstrap-${ARCH}.zip"
