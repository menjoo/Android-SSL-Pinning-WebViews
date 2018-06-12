package com.example.mennomorsink.webview.config

import com.example.mennomorsink.webview.DomainWhitelistInterceptor
import com.example.mennomorsink.webview.SchemeWhitelistInterceptor
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object Config {

    fun createSecureOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(createLoggingInterceptor())
                .addInterceptor(createSchemeWhitelistInterceptor())
                .addInterceptor(createDomainWhitelistInterceptor())
                .certificatePinner(createCertificatePinner())
                .build()
    }

    fun createPinningOnlyOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(createLoggingInterceptor())
                .certificatePinner(createCertificatePinner())
                .build()
    }

    fun createWhitelistingOnlyOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(createLoggingInterceptor())
                .addInterceptor(createSchemeWhitelistInterceptor())
                .addInterceptor(createDomainWhitelistInterceptor())
                .build()
    }

    fun createInsecureOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(createLoggingInterceptor())
                .build()
    }

    /**
     * Certificate pinning config
     *
     * To set this up, start with something like this:
     *
     * .add("yourdomain.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
     *
     * Then check the logs to see the chain of the certificates that are being
     * served. Each is a hash of the certificate's public key. You can copy
     * these to setup your pinning.
     *
     * Peer certificate chain:
     * sha256/jZTHOvQmgAA3TNS56Wqdn6indUj5L4YFcjT/BSP412c=: CN=infosupport.com,OU=PositiveSSL Multi-Domain,OU=Domain Control Validated
     * sha256/klO23nT2ehFDXCfx3eHTDRESMz3asj1muO+4aIdjiuY=: CN=COMODO RSA Domain Validation Secure Server CA,O=COMODO CA Limited,L=Salford,ST=Greater Manchester,C=GB
     * sha256/grX4Ta9HpZx6tSHkmCrvpApTQGo67CYDnvprLg5yRME=: CN=COMODO RSA Certification Authority,O=COMODO CA Limited,L=Salford,ST=Greater Manchester,C=GB
     * Pinned certificates for www.infosupport.com:
     * sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
     */
    private fun createCertificatePinner(): CertificatePinner {
        return CertificatePinner.Builder()
                .add(Pinning.DOMAIN_INFOSUPPORT_COM, Pinning.CERT_COMODO_INFOSUPPORT_COM)
                .add(Pinning.DOMAIN_INFOSUPPORT_COM, Pinning.CERT_COMODO_INTERMEDIATE)
                .add(Pinning.DOMAIN_INFOSUPPORT_COM, Pinning.CERT_COMODO_CA)
                .build()
    }

    /**
     * Whitelist of schemes
     *
     * Add schemes you want to allow to be here
     */
    private fun createSchemeWhitelistInterceptor() = SchemeWhitelistInterceptor(arrayOf(
            Whitelist.SCHEME_HTTPS
    ))

    /**
     * Whitelist of domains
     *
     * Add the domains you want to allow here
     */
    private fun createDomainWhitelistInterceptor() = DomainWhitelistInterceptor(arrayOf(
            Whitelist.DOMAIN_INFOSUPPORT_COM
    ))

    /**
     * Loggin config
     */
    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        return loggingInterceptor
    }

}
