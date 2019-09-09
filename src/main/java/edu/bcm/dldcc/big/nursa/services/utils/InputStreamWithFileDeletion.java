package edu.bcm.dldcc.big.nursa.services.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by alexey on 4/6/15.
 */
public class InputStreamWithFileDeletion extends FileInputStream {

    File f;

    public InputStreamWithFileDeletion(File file) throws FileNotFoundException {
        super(file);
        f = file;
    }

    @Override
    public void close() throws IOException {
        super.close();
        f.delete();
    }
}
