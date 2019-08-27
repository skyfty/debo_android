package com.qcwl.debo.utils;

import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by xxm on 2018/8/27.
 */
public class RAS {
    //public static final String TAG = "RAG======";
    //字符串公钥，可以直接保存在客户端
    public static final String PUBLIC_KEY_STR = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCsr+2TFd+AnMEyfQuwJiwAHc6Q\n" +
            "Qvzorme66XQSIoiwEpFKvRktiwAWqnw0Y84JTdBQTS6jknJu585/dI+qJKj4TBKk\n" +
            "Rl2bcqouPbqveJRA929872qskIKaiymmzAqEXP6HsxfDk7zVoZnEZAOm5CQU6+5a\n" +
            "K19SXzQSF1Lh0tEV+wIDAQAB";

    private static int sBase64Mode = Base64.DEFAULT;

    public RAS() {

    }

    /**
     * @@return  参数直接使用该方法
     */
    public static String getPublicKeyStrRAS(byte[] srcData) {
        PublicKey publicKey = keyStrToPublicKey(PUBLIC_KEY_STR);
        return encryptDataByPublicKey(srcData, publicKey);
    }

    /*
   使用公钥加密数据，结果用Base64转码
*/
    public static String encryptDataByPublicKey(byte[] srcData, PublicKey publicKey) {
        byte[] resultBytes = processData(srcData, publicKey, Cipher.ENCRYPT_MODE);
        String s = "";
        try {
            s = Base64.encodeToString(resultBytes, Base64.DEFAULT);
        }catch (Exception e){
            e.printStackTrace();
            s="";
        }

        return s;

    }
        /*
        使用私钥加密，结果用Base64转码
     */

    public static String encryptDataByPrivateKey(byte[] srcData, PrivateKey privateKey) {

        byte[] resultBytes = processData(srcData, privateKey, Cipher.ENCRYPT_MODE);
        return Base64.encodeToString(resultBytes, Base64.DEFAULT);
    }

    /*
    加密或解密数据的通用方法
    @param srcData
    待处理的数据
    @param key
    公钥或者私钥
    @param mode
    指定是加密还是解密，值为Cipher.ENCRYPT_MODE或者Cipher.DECRYPT_MODE

 */
    private static byte[] processData(byte[] srcData, Key key, int mode) {

        //用来保存处理结果
        byte[] resultBytes = null;

        try {
            //构建Cipher对象，需要传入一个字符串，格式必须为"algorithm/mode/padding"或者"algorithm/",意为"算法/加密模式/填充方式"
            Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding");
            //初始化Cipher，mode指定是加密还是解密，key为公钥或私钥
            cipher.init(mode, key);
            //处理数据
            //限制128b长度字节
            resultBytes = cipher.doFinal(srcData);
           /* resultBytes = encryptWithPublicKeyBlock(srcData,Base64.decode(PUBLIC_KEY_STR, sBase64Mode));
            Log.i(TAG,"......srcData33333="+resultBytes.length);*/
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultBytes;
    }

    /*
       使用私钥解密，返回解码数据
    */
    public static byte[] decryptDataByPrivate(String encryptedData, PrivateKey privateKey) {

        byte[] bytes = Base64.decode(encryptedData, Base64.DEFAULT);

        return processData(bytes, privateKey, Cipher.DECRYPT_MODE);
    }

    /*
        使用私钥进行解密，解密数据转换为字符串，使用utf-8编码格式
     */
    public static String decryptedToStrByPrivate(String encryptedData, PrivateKey privateKey) {
        return new String(decryptDataByPrivate(encryptedData, privateKey));
    }

    /*
        使用私钥解密，解密数据转换为字符串，并指定字符集
     */
    public static String decryptedToStrByPrivate(String encryptedData, PrivateKey privateKey, String charset) {
        try {

            return new String(decryptDataByPrivate(encryptedData, privateKey), charset);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
        /*
        将字符串形式的公钥转换为公钥对象
     */

    public static PublicKey keyStrToPublicKey(String publicKeyStr) {
        PublicKey publicKey = null;
        byte[] keyBytes = Base64.decode(publicKeyStr, sBase64Mode);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    /*
        将字符串形式的私钥，转换为私钥对象
     */

    public static PrivateKey keyStrToPrivate(String privateKeyStr) {

        PrivateKey privateKey = null;

        byte[] keyBytes = Base64.decode(privateKeyStr, sBase64Mode);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);

        try {

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            privateKey = keyFactory.generatePrivate(keySpec);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return privateKey;

    }


    private static int KEYSIZE = 1024;// 密钥位数
    private static int RESERVE_BYTES = 11;
    private static int DECRYPT_BLOCK = KEYSIZE / 8;
    private static int ENCRYPT_BLOCK = DECRYPT_BLOCK - RESERVE_BYTES;
    public static final String ECB_PADDING = "RSA/ECB/PKCS1Padding";//加密填充方式
    public static final String RSA = "RSA";// 非对称加密密钥算法
    public static PublicKey getPublicKey(byte[] key) throws Exception{
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePublic(keySpec);
    }

    public static byte[] encryptWithPublicKeyBlock(byte[] data, byte[] key) throws Exception{
        int blockCount = (data.length / ENCRYPT_BLOCK);

        if ((data.length % ENCRYPT_BLOCK) != 0) {
            blockCount += 1;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream(blockCount * ENCRYPT_BLOCK);
        Cipher cipher = Cipher.getInstance(ECB_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(key));

        for (int offset = 0; offset < data.length; offset += ENCRYPT_BLOCK) {

            int inputLen = (data.length - offset);
            if (inputLen > ENCRYPT_BLOCK) {
                inputLen = ENCRYPT_BLOCK;
            }
            byte[] encryptedBlock = cipher.doFinal(data, offset, inputLen);
            bos.write(encryptedBlock);
        }

        bos.close();
        return bos.toByteArray();
    }

}




