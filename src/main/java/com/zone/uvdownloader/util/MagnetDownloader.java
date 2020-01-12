package com.zone.uvdownloader.util;

import bt.Bt;
import bt.data.Storage;
import bt.data.file.FileSystemStorage;
import bt.dht.DHTConfig;
import bt.dht.DHTModule;
import bt.runtime.BtClient;
import com.google.inject.Module;

import java.io.File;
import java.nio.file.Path;

public class MagnetDownloader {
    public void start(){
//        Config config = new Config() {
//            @Override
//            public int getNumOfHashingThreads() {
//                return Runtime.getRuntime().availableProcessors() * 2;
//            }
//        };

// enable bootstrapping from public routers
        Module dhtModule = new DHTModule(new DHTConfig() {
            @Override
            public boolean shouldUseRouterBootstrap() {
                return true;
            }
        });

// get download directory
        Path targetDirectory = new File("d://Downloads").toPath();

// create file system based backend for torrent data
        Storage storage = new FileSystemStorage(targetDirectory);

// create client with a private runtime
        BtClient client = Bt.client()
//                .config(config)
                .storage(storage)
                .magnet("magnet:?xt=urn:btih:b398f33d5fdc26bc0b79f69dbacfdb31bc5275aa")
                .autoLoadModules()
                .module(dhtModule)
                .stopWhenDownloaded()
                .build();

// launch
        client.startAsync(state->{
            System.out.println(state.getPiecesRemaining());
        },1000).join();
    }

    public static void main(String[] args) {
        new MagnetDownloader().start();
    }
}
