package de.njsm.stocks.client.init;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.exceptions.CryptoException;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import javax.security.auth.x500.X500Principal;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

public class KeyStoreHandlerImpl implements KeystoreHandler {

    private static final Logger LOG = LogManager.getLogger(KeyStoreHandlerImpl.class);

    private static final int keySize = 4096;
    private static final String keyAlgName = "RSA";
    private static final String sigAlgName = "SHA256WithRSA";

    private KeyStore keystore;
    private KeyPair clientKeys;
    private Certificate caCert;
    private Certificate intermediateCert;
    private Certificate clientCert;


    public KeyStoreHandlerImpl() throws CryptoException {
        try {
            keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(null);
        } catch (KeyStoreException |
                IOException |
                CertificateException |
                NoSuchAlgorithmException e) {
            LOG.error("Cannot create new keystore", e);
            throw new CryptoException("Cannot create keys");
        }
    }

    @Override
    public void generateNewKey() throws CryptoException {
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance(keyAlgName);
            gen.initialize(keySize);
            clientKeys = gen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            LOG.error(e);
            throw new CryptoException("Crypto algorithm not available");
        }
    }

    @Override
    public String generateCsr(String subjectName) throws CryptoException {
        try {
            X500Principal principal = new X500Principal("CN=" + subjectName +
                    ",OU=User,O=stocks");
            ContentSigner signGen = new JcaContentSignerBuilder(sigAlgName).build(clientKeys.getPrivate());
            PKCS10CertificationRequestBuilder builder =
                    new JcaPKCS10CertificationRequestBuilder(principal, clientKeys.getPublic());
            PKCS10CertificationRequest csr = builder.build(signGen);
            return convertCsrToString(csr);
        } catch (OperatorCreationException |
                IOException e) {
            throw new CryptoException("CSR generation failed", e);
        }
    }

    @Override
    public String getFingerPrintFromPem(String pemFile) throws CryptoException {
        try {
            Certificate certificate = convertToCertificate(pemFile);
            return computeFingerprint(certificate);
        } catch (CertificateException e) {
            throw new CryptoException("Could not open certificate", e);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("SHA-256 is not available");
        }
    }

    @Override
    public void importCaCertificate(String pemCertificate) throws CryptoException {
        try {
            Certificate certificate = convertToCertificate(pemCertificate);
            keystore.setCertificateEntry("ca", certificate);
            caCert = certificate;
        } catch (CertificateException e) {
            throw new CryptoException("Could not convert certificate");
        } catch (KeyStoreException e) {
            throw new CryptoException("Could not store certificate");
        }
    }

    @Override
    public void importIntermediateCertificate(String pemCertificate) throws CryptoException {
        try {
            Certificate certificate = convertToCertificate(pemCertificate);
            keystore.setCertificateEntry("chain", certificate);
            intermediateCert = certificate;
        } catch (CertificateException e) {
            throw new CryptoException("Could not convert certificate");
        } catch (KeyStoreException e) {
            throw new CryptoException("Could not store certificate");
        }
    }

    @Override
    public void importClientCertificate(String pemCertificate) throws CryptoException {
        try {
            clientCert = convertToCertificate(pemCertificate);
            Certificate[] trustChain = new Certificate[3];
            trustChain[0] = clientCert;
            trustChain[1] = intermediateCert;
            trustChain[2] = caCert;
            keystore.setKeyEntry("client",
                    clientKeys.getPrivate(),
                    Configuration.KEYSTORE_PASSWORD.toCharArray(),
                    trustChain);
        } catch (CertificateException e) {
            throw new CryptoException("Could not convert certificate", e);
        } catch (KeyStoreException e) {
            throw new CryptoException("Could not store certificate", e);
        }
    }

    @Override
    public void store() throws IOException {
        try {
            FileOutputStream fileWriter = new FileOutputStream(Configuration.KEYSTORE_PATH);
            BufferedOutputStream writer = new BufferedOutputStream(fileWriter);
            keystore.store(writer, Configuration.KEYSTORE_PASSWORD.toCharArray());
            writer.close();
        } catch (NoSuchAlgorithmException |
                CertificateException |
                KeyStoreException e) {
            LOG.error("Error storing keystore", e);
            throw new IOException("Error storing keystore", e);
        }
    }

    @Override
    public KeyStore getKeyStore() {
        return keystore;
    }

    private String convertCsrToString(PKCS10CertificationRequest csr) throws IOException {
        PemObject object = new PemObject("CERTIFICATE REQUEST", csr.getEncoded());
        StringWriter writer = new StringWriter();
        PemWriter pemWriter = new PemWriter(writer);
        pemWriter.writeObject(object);
        pemWriter.close();
        return writer.toString();
    }

    private String computeFingerprint(Certificate certificate) throws NoSuchAlgorithmException, CertificateEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(certificate.getEncoded());
        return convertBytesToString(md.digest());
    }

    private String convertBytesToString(byte[] digest) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuilder buf = new StringBuilder(digest.length * 3);
        for (byte aDigest : digest) {
            buf.append(hexDigits[(aDigest & 0xf0) >> 4]);
            buf.append(hexDigits[aDigest & 0x0f]);
            buf.append(":");
        }
        buf.delete(buf.length()-1, buf.length());

        return buf.toString();
    }

    private Certificate convertToCertificate(String pemFile) throws CertificateException {
        StringReader reader = new StringReader(pemFile);
        ReaderInputStream stream = new ReaderInputStream(reader);
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        return factory.generateCertificate(stream);
    }

    KeyPair getClientKeys() {
        return clientKeys;
    }
}
