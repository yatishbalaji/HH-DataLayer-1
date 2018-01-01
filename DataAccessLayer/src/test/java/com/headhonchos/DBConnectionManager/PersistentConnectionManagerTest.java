package com.headhonchos.DBConnectionManager;

import org.junit.Test;

import java.sql.Connection;

public class PersistentConnectionManagerTest {

    @Test
    public void testGetAbcReadConnection() throws Exception {
        Connection abcReadConnection1 = PersistentConnectionManager.getAbcReadConnection();

        Connection abcReadConnection2 = PersistentConnectionManager.getAbcReadConnection();
        if(abcReadConnection1 == abcReadConnection2){
            System.out.println("same second Connection.");
        }
        else{
            System.out.println("OOPS different SS. - ");
        }

        abcReadConnection1.close();
        Connection abcReadConnection3 = PersistentConnectionManager.getAbcReadConnection();
        if(abcReadConnection1 == abcReadConnection3){
            System.out.println("same third Connection.");
        }
        else{
            System.out.println("FINE 1<>3");
        }
    }

    @Test
    public void testGetAbcWriteConnection() throws Exception {

    }

    @Test
    public void testGetAbcResumeSkillConnection() throws Exception {

    }
}