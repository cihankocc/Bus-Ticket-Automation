module Otomasyon {
    requires java.desktop;
    requires transitive java.sql;
    requires java.base;

    exports utils;
    exports controllers;
    exports models;
    exports gui;
}
