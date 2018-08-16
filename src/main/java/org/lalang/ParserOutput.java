package org.lalang;

public class ParserOutput {

    StringBuffer content;
    boolean modified; //flag to detect if output has already been set

    public ParserOutput() {
        content = new StringBuffer();
        modified = false;
    }

    public void println(String text) {
        if(!modified) modified = true;
        content.append(text);
        content.append("\n");
    }
    
    public boolean ismodified() {
        return modified;
    }

    @Override
    public String toString() {
        return content.toString();
    }
}