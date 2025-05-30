package com.example.zzserver.config;

import org.jasypt.util.text.BasicTextEncryptor;

public class EncryptorTool {
    public static void main(String[] args) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword("zz-server1313"); // 환경변수에 설정한 키와 동일해야 함


        String encryptedPassword = textEncryptor.encrypt("zz-server12");
        System.getenv("JASYPT_ENCRYPTOR_PASSWORD");
        System.out.println("Encrypted password: " + encryptedPassword);
    }

}
