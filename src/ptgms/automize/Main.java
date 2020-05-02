package ptgms.automize;

import java.io.*;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.compress.compressors.bzip2.*;

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
        byte[] buffer = new byte[1024];
        try {
            Process process2 = processBuilderInit.start();
            int exitVal = process2.waitFor();
            GZIPOutputStream os =
            new GZIPOutputStream(new FileOutputStream("Packages.gz"));
            FileInputStream in =
            new FileInputStream("Packages");
            int totalSize;
            while((totalSize = in.read(buffer)) > 0 ) {
                os.write(buffer, 0, totalSize);
            }
            in.close();
            os.finish();
            os.close();
            
            FileInputStream in2 =
            new FileInputStream("Packages");
            FileOutputStream fos = new FileOutputStream("Packages.bz2");
            
            byte[] bytes = in2.readAllBytes();
            try (InputStream is = new ByteArrayInputStream(bytes)) {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                try (OutputStream os2 = new BZip2CompressorOutputStream(bout, 1)) {
                    byte[] buf = new byte[4096];
                    int len = 0;
                    while ((len = is.read(buf, 0, buf.length)) != -1) {
                        os2.write(buf, 0, len);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
                /* strip the header from the byte array and return it */
                bytes = bout.toByteArray();
                byte[] bzip2 = new byte[bytes.length - 2];
                System.arraycopy(bytes, 2, bzip2, 0, bzip2.length);
                fos.write(bzip2);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
