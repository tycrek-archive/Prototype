package dev.jmoore;

import com.github.steveice10.mc.auth.exception.request.RequestException;
import com.github.steveice10.mc.auth.service.MsaAuthenticationService;
import com.github.steveice10.mc.auth.util.MSALApplicationOptions;
import lombok.Setter;

import java.io.IOException;
import java.util.function.Consumer;

public class Account {

    private static final String CLIENT_ID = "AZURE-CLIENT-ID";
    private final MsaAuthenticationService auth;
    @Setter private Consumer<String> deviceCodeConsumer = System.out::println;

    public Account() throws IOException {
        this(CLIENT_ID);
    }

    public Account(String clientId) throws IOException {
        this.auth = new MsaAuthenticationService(clientId, new MSALApplicationOptions.Builder().offlineAccess(true).build());
    }

    public MsaAuthenticationService getAuth() {
        return this.auth;
    }

    public void login() throws RequestException {
        System.out.println("Logging in...");

        // This is where the magic happens
        if (!this.auth.isLoggedIn()) {

            // Send onboarding request
            this.auth.setDeviceCodeConsumer((deviceCode) -> deviceCodeConsumer.accept(deviceCode.message()));

            // Wait for user to login
            this.auth.login();

            // Print the username to the console
            System.out.printf("Logged in as %s (%s)",
                    this.auth.getSelectedProfile().getName(),
                    this.auth.getSelectedProfile().getId());
        }
    }
}
