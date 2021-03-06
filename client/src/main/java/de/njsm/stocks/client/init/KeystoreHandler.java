package de.njsm.stocks.client.init;

import de.njsm.stocks.client.exceptions.CryptoException;

import java.io.IOException;
import java.security.KeyStore;

public interface KeystoreHandler {

    void generateNewKey() throws CryptoException;

    String generateCsr(String subjectName) throws CryptoException;

    String getFingerPrintFromPem(String pemFile) throws CryptoException;

    void importCaCertificate(String pemCertificate) throws CryptoException;

    void importIntermediateCertificate(String pemCertificate) throws CryptoException;

    void importClientCertificate(String pemCertificate) throws CryptoException;

    KeyStore getKeyStore();

    void store() throws IOException;
}
