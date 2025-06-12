package com.example.zzserver.config;

import org.jasypt.util.text.BasicTextEncryptor;

import java.util.Scanner;

public class EncryptorTool {
    public static void main(String[] args) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        String encryptorPassword = System.getenv("JASYPT-ENCRYPTOR-PASSWORD");

        if (encryptorPassword == null || encryptorPassword.isEmpty()) {
            throw new IllegalStateException("환경변수 'JASYPT-ENCRYPTOR-PASSWORD'가 설정되지 않았습니다.");
        }
        textEncryptor.setPassword(encryptorPassword);

        Scanner scanner = new Scanner(System.in);
        System.out.print("암호화할 비밀번호를 입력하세요: ");
        String plainPassword = scanner.nextLine();

        String encryptedPassword = textEncryptor.encrypt(plainPassword);
        System.out.println("Encrypted password: ENC(" + encryptedPassword + ")");
    }
}
