package ptgms.automize;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
	    System.out.println("Deleting old Package Files...");
        File f= new File("Packages.bz2");
        File f2= new File("Packages.gz");
        if (f.exists() && f2.exists()) {
            if (f.delete() && f2.delete()) {
                System.out.println("Both deleted, generating new...");
            } else {
                System.out.println("Error deleting old Package files. Please manually delete them.");
                System.exit(-1);
            }
        }
        ProcessBuilder processBuilderInit = new ProcessBuilder();
        processBuilderInit.command("bash", "-c", "dpkg-scanpackages Files /dev/null > Packages");
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", "gzip -c9 Packages > Packages.gz");
        ProcessBuilder processBuilder2 = new ProcessBuilder();
        processBuilder2.command("bash", "-c", "bzip2 -c9 Packages > Packages.bz2");
        try {
            Process process2 = processBuilderInit.start();
            Process process = processBuilder.start();
            Process process1 = processBuilder2.start();
            int exitVal = process.waitFor();
            int exitVal2 = process1.waitFor();
            int exitVal3 = process2.waitFor();
            if (exitVal != 1 && exitVal2 != 1 && exitVal3 != 1) {
                System.out.println("Successfully created new Package files.");
            } else {
                System.out.println("Values: " + exitVal3 + exitVal + exitVal);
                System.out.println("Could not make package files. Please do it manually.");
                System.exit(-1);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
