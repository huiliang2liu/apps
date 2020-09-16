package com.base.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class MemoryUtil {
//    MemTotal: 所有可用RAM大小（即物理内存减去一些预留位和内核的二进制代码大小）
//
//    MemFree: LowFree与HighFree的总和，被系统留着未使用的内存
//
//    Buffers: 用来给文件做缓冲大小
//
//    Cached: 被高速缓冲存储器（cache memory）用的内存的大小（等于 diskcache minus SwapCache ）.
//
//    SwapCached:被高速缓冲存储器（cache memory）用的交换空间的大小，已经被交换出来的内存，但仍然被存放在swapfile                    中。用来在需要的时候很快的被替换而不需要再次打开I/O端口。
//
//    Active: 在活跃使用中的缓冲或高速缓冲存储器页面文件的大小，除非非常必要否则不会被移作他用.
//
//    Inactive: 在不经常使用中的缓冲或高速缓冲存储器页面文件的大小，可能被用于其他途径.
//
//    HighTotal:
//
//    HighFree: 该区域不是直接映射到内核空间。内核必须使用不同的手法使用该段内存。
//
//    LowTotal:
//
//    LowFree: 低位可以达到高位内存一样的作用，而且它还能够被内核用来记录一些自己的数据结构。Among many
//
//　　　　　other things, it is where everything from the Slab is allocated. Bad things happen when you’re out 　　　　　of lowmem.
//
//    SwapTotal: 交换空间的总大小
//
//    SwapFree: 未被使用交换空间的大小
//
//    Dirty: 等待被写回到磁盘的内存大小。
//
//    Writeback: 正在被写回到磁盘的内存大小。
//
//    AnonPages：未映射页的内存大小
//
//    Mapped: 设备和文件等映射的大小。
//
//    Slab: 内核数据结构缓存的大小，可以减少申请和释放内存带来的消耗。
//
//    SReclaimable:可收回Slab的大小
//
//    SUnreclaim：不可收回Slab的大小（SUnreclaim+SReclaimable＝Slab）
//
//    PageTables：管理内存分页页面的索引表的大小。
//
//    NFS_Unstable:不稳定页表的大小
//
//    VmallocTotal: 可以vmalloc虚拟内存大小
//
//    VmallocUsed: 已经被使用的虚拟内存大小。
//
//    VmallocChunk: largest contigious block of vmalloc area which is free
    private static final String TAG = "MemoryUtil";
    private static MemoryUtil util;
    private static final Long KB = 1024L;
    private static final Long M = KB * 1024L;
    private static final Long G = M * 1024;
    private Map<String, Long> memoryMap = new HashMap<>();

    public static MemoryUtil getInstance() {
        if (util == null)
            synchronized (TAG) {
                if (util == null)
                    util = new MemoryUtil();
            }
        return util;
    }

    public long getMemTotal() {
        return get("MemTotal");
    }

    public long getMemFree() {
        return get("MemFree");
    }

    public long getBuffers() {
        return get("Buffers");
    }

    public long getCached() {
        return get("Cached");
    }

    public long getSwapCached() {
        return get("SwapCached");
    }

    public long getActive() {
        return get("Active");
    }

    public long getInactive() {
        return get("Inactive");
    }

    public long getActiveAnon() {
        return get("Active(anon)");
    }

    public long getInactiveAnon() {
        return get("Inactive(anon)");
    }

    public long getActiveFile() {
        return get("Active(file)");
    }

    public long getInactiveFile() {
        return get("Inactive(file)");
    }

    public long getUnevictable() {
        return get("Unevictable");
    }

    public long getMlocked() {
        return get("Mlocked");
    }

    public long getSwapTotal() {
        return get("SwapTotal");
    }

    public long getSwapFree() {
        return get("SwapFree");
    }

    public long getDirty() {
        return get("Dirty");
    }

    public long getWriteback() {
        return get("Writeback");
    }

    public long getAnonPages() {
        return get("AnonPages");
    }

    public long getMapped() {
        return get("Mapped");
    }

    public long getShmem() {
        return get("Shmem");
    }

    public long getSlab() {
        return get("Slab");
    }

    public long getSReclaimable() {
        return get("SReclaimable");
    }

    public long getSUnreclaim() {
        return get("SUnreclaim");
    }

    public long getKernelStack() {
        return get("KernelStack");
    }

    public long getPageTables() {
        return get("PageTables");
    }

    public long getNFS_Unstable() {
        return get("NFS_Unstable");
    }

    public long getBounce() {
        return get("Bounce");
    }

    public long getWritebackTmp() {
        return get("WritebackTmp");
    }

    public long getCommitLimit() {
        return get("CommitLimit");
    }

    public long getCommitted_AS() {
        return get("Committed_AS");
    }

    public long getVmallocTotal() {
        return get("VmallocTotal");
    }

    public long getVmallocUsed() {
        return get("VmallocUsed");
    }

    public long getVmallocChunk() {
        return get("VmallocChunk");
    }

    private long get(String name) {
        Long l = null;
        if (memoryMap.containsKey(name))
            l = memoryMap.get(name);
        return l == null ? l : 0;
    }

    private MemoryUtil() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("proc/meminfo"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;
                String[] sp = line.split(":");
                String name = sp[0].trim().toLowerCase();
                if (name.isEmpty())
                    continue;
                sp = sp[1].trim().split(" ");
                long size = Long.valueOf(sp[0].trim());
                String unit = sp.length > 1 ? sp[2].trim().toLowerCase() : "kb";
                if (unit.equals("kb")) {
                    memoryMap.put(name, size * KB);
                } else if (unit.equals("m")) {
                    memoryMap.put(name, size * M);
                } else if (unit.equals("G")) {
                    memoryMap.put(name, size * G);
                } else
                    memoryMap.put(name, size);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    private static final String MART = "%s:%s,";

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        for (String key : memoryMap.keySet()) {
            sb.append(String.format(MART, key, memoryMap.get(key)));
        }
        sb.setLength(sb.length() - 1);
        sb.append("}");
        return super.toString();
    }
}
