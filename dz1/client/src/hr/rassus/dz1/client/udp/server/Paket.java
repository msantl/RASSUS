package hr.rassus.dz1.client.udp.server;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: msantl
 * Date: 10/17/13
 * Time: 3:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class Paket {
    public static int PaketSize = 1024 * 10;
    public static int ContentSize = PaketSize - 4;

    public byte[] data = new byte[PaketSize];

    private boolean received;

    public Paket() {
        received = false;
    }

    public boolean isReceived() {
        return received;
    }

    public void setReceived() {
        received = true;
    }

    public void setPaketNumber(int number) {
        String num = Integer.toString(number);
        while (num.length() < 4) {
            num = "0" + num;
        }

        for (int i = 0; i < 4; ++i) {
            data[i] = (byte) num.charAt(i);
        }
    }

    public void setPaketContent(byte[] content) {
        for (int i = 0; i < content.length; ++i) {
            data[i + 4] = content[i];
        }

        for (int i = content.length; i < ContentSize; ++i) {
            data[i+4] = (byte) 0;
        }
    }

    public int getPaketNumber() {
        return Integer.parseInt(new String(Arrays.copyOfRange(data, 0, 4)));
    }

    public byte[] getPaketContent() {
        return Arrays.copyOfRange(data, 4, PaketSize);
    }

}
