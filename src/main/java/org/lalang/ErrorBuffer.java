package org.lalang;

class ErrorBuffer {
    private StringBuffer buffer;
    private boolean isModified;

    public ErrorBuffer() {
        this.buffer = new StringBuffer();
        this.isModified = false;
    }

    public void println(String texto) {
        this.buffer.append(texto + "\n");
        this.isModified = true;
    }

    @Override
    public String toString() {
        return this.buffer.toString();
    }

    public boolean modified() {
        return this.isModified;
    }
}