package com.afdUtils;

import com.dev.jna.Library;
import com.dev.jna.Native;
import com.dev.jna.Platform;
import com.dev.jna.Pointer;

public interface CLibrary extends Library {
    CLibrary INSTANCE = (CLibrary) Native.loadLibrary((Platform.isWindows() ? "msvcrt" : "c"),CLibrary.class);

    Pointer malloc(int len);
    void free(Pointer p);
    void printf(String format, Object... args);
    Pointer memcpy(Pointer dst, Pointer src, long size);
}