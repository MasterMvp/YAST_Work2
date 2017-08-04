//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package printpp.printpp_yt;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.wlpava.printer.jni.JniUtils;

import java.io.UnsupportedEncodingException;

import printpp.printpp_yt.BluetoothPort;

public class PrintPP_CPCL {
    private BluetoothPort Port = null;
    private int PortTimeOut = 3000;
    private int Times = -1;
    private int Family = 1;
    public static final int STATE_NOPAPER_UNMASK = 1;
    public static final int STATE_OVERHEAT_UNMASK = 2;
    public static final int STATE_BATTERYLOW_UNMASK = 4;
    public static final int STATE_PRINTING_UNMASK = 8;
    public static final int STATE_COVEROPEN_UNMASK = 16;

    public PrintPP_CPCL() {
        this.Port = new BluetoothPort();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void connect(String address) {
        if (this.Port.isOpen) {
            this.Port.close();
        }

        this.Port.open(address, this.PortTimeOut);
    }

    public boolean connect(String name, String address) {
        if (!isEmpty(address) && !isEmpty(name) && name.startsWith(JniUtils.printerPrefix())) {
            if (this.Port.isOpen) {
                this.Port.close();
            }

            this.Port.open(address, this.PortTimeOut);
            return true;
        } else {
            return false;
        }
    }

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    public void disconnect() {
        if (this.Port.isOpen) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException var2) {
                var2.printStackTrace();
            }

            this.Port.close();
        }

    }

    public boolean isConnected() {
        return this.Port.isOpen;
    }

    public Bitmap GetBitmap() {
        return null;
    }

    boolean portSendCmd(String cmd) {
        if (this.Port.isOpen) {
            cmd = cmd + "\r\n";
            byte[] data = (byte[]) null;

            try {
                data = cmd.getBytes("GBK");
            } catch (UnsupportedEncodingException var6) {
                return false;
            }

            int Len = data.length;

            while (true) {
                try {
                    Thread.sleep(1L);
                } catch (InterruptedException var5) {
                    var5.printStackTrace();
                }

                if (Len <= 10) {
                    return this.Port.write(data, data.length - Len, Len);
                }

                if (!this.Port.write(data, data.length - Len, 10)) {
                    return false;
                }

                Len -= 10;
            }
        } else {
            return false;
        }
    }

    public void print(int horizontal, int skip) {
        String cmd;
        if (horizontal == 0) {
            cmd = JniUtils.print1();
            this.portSendCmd(cmd);
        } else {
            cmd = JniUtils.print2();
            this.portSendCmd(cmd);
        }

        if (skip > 0) {
            this.feed();
        }

        --this.Times;
    }

    public void pageSetup(int pageWidth, int pageHeight) {
        ++this.Times;
        String cmd = JniUtils.pageSetup1() + pageHeight + " " + "1";
        this.portSendCmd(cmd);
        cmd = JniUtils.pageSetup2() + pageWidth;
        this.portSendCmd(cmd);
    }

    public void drawBox(int lineWidth, int top_left_x, int top_left_y, int bottom_right_x, int bottom_right_y) {
        if (top_left_x > 575) {
            top_left_x = 575;
        }

        if (bottom_right_x > 575) {
            bottom_right_x = 575;
        }

        String CPCLCmd = JniUtils.drawBox() + top_left_x + " " + top_left_y + " " + bottom_right_x + " " + bottom_right_y + " " + lineWidth;
        this.portSendCmd(CPCLCmd);
    }

    public void drawLine(int lineWidth, int start_x, int start_y, int end_x, int end_y, boolean fullline) {
        if (start_x > 575) {
            start_x = 575;
        }

        if (end_x > 575) {
            end_x = 575;
        }

        String CPCLCmd;
        if (fullline) {
            CPCLCmd = JniUtils.drawLine1() + start_x + " " + start_y + " " + end_x + " " + end_y + " " + lineWidth;
        } else {
            CPCLCmd = JniUtils.drawLine2() + start_x + " " + start_y + " " + end_x + " " + end_y + " " + lineWidth;
        }

        this.portSendCmd(CPCLCmd);
    }

    public void drawText(int text_x, int text_y, String text, int fontSize, int rotate, int bold, boolean reverse, boolean underline) {
        String CPCLCmd;
        if (underline) {
            CPCLCmd = JniUtils.drawText1();
        } else {
            CPCLCmd = JniUtils.drawText2();
        }

        this.portSendCmd(CPCLCmd);
        CPCLCmd = JniUtils.drawText3() + bold;
        this.portSendCmd(CPCLCmd);
        boolean family = false;
        byte size = 0;
        byte ex = 1;
        byte ey = 1;
        int family1;
        switch (fontSize) {
            case 1:
                family1 = 55;
                break;
            case 2:
                family1 = this.Family;
                break;
            case 3:
                family1 = 4;
                break;
            case 4:
                family1 = this.Family;
                ex = 2;
                ey = 2;
                break;
            case 5:
                family1 = 4;
                ex = 2;
                ey = 2;
                break;
            case 6:
                family1 = this.Family;
                ex = 4;
                ey = 4;
                break;
            case 7:
                family1 = 4;
                ex = 3;
                ey = 3;
                break;
            default:
                family1 = this.Family;
        }

        CPCLCmd = JniUtils.drawText4() + ex + " " + ey;
        this.portSendCmd(CPCLCmd);
        switch (rotate) {
            case 1:
                if (reverse) {
                    CPCLCmd = JniUtils.drawText6() + family1 + " " + size + " " + text_x + " " + text_y + " " + text;
                    this.portSendCmd(CPCLCmd);
                } else {
                    CPCLCmd = JniUtils.drawText10() + family1 + " " + size + " " + text_x + " " + text_y + " " + text;
                    this.portSendCmd(CPCLCmd);
                }
                break;
            case 2:
                if (reverse) {
                    CPCLCmd = JniUtils.drawText7() + family1 + " " + size + " " + text_x + " " + text_y + " " + text;
                    this.portSendCmd(CPCLCmd);
                } else {
                    CPCLCmd = JniUtils.drawText11() + family1 + " " + size + " " + text_x + " " + text_y + " " + text;
                    this.portSendCmd(CPCLCmd);
                }
                break;
            case 3:
                if (reverse) {
                    CPCLCmd = JniUtils.drawText8() + family1 + " " + size + " " + text_x + " " + text_y + " " + text;
                    this.portSendCmd(CPCLCmd);
                } else {
                    CPCLCmd = JniUtils.drawText12() + family1 + " " + size + " " + text_x + " " + text_y + " " + text;
                    this.portSendCmd(CPCLCmd);
                }
                break;
            default:
                if (reverse) {
                    CPCLCmd = JniUtils.drawText5() + family1 + " " + size + " " + text_x + " " + text_y + " " + text;
                    this.portSendCmd(CPCLCmd);
                } else {
                    CPCLCmd = JniUtils.drawText9() + family1 + " " + size + " " + text_x + " " + text_y + " " + text;
                    this.portSendCmd(CPCLCmd);
                }
        }

    }

    public void drawText(int text_x, int text_y, int width, int height, String str, int fontsize, int rotate, int bold, boolean underline, boolean reverse) {
        String CPCLCmd;
        if (underline) {
            CPCLCmd = JniUtils.drawText1();
        } else {
            CPCLCmd = JniUtils.drawText2();
        }

        this.portSendCmd(CPCLCmd);
        CPCLCmd = JniUtils.drawText3() + bold;
        this.portSendCmd(CPCLCmd);
        boolean family = false;
        byte size = 0;
        byte ex = 1;
        byte ey = 1;
        boolean Height = false;
        int Width = 0;
        int var20;
        byte var21;
        switch (fontsize) {
            case 1:
                var20 = 55;
                var21 = 16;
                break;
            case 2:
                var20 = this.Family;
                var21 = 24;
                break;
            case 3:
                var20 = 4;
                var21 = 32;
                break;
            case 4:
                var20 = this.Family;
                var21 = 48;
                ex = 2;
                ey = 2;
                break;
            case 5:
                var20 = 4;
                var21 = 64;
                ex = 2;
                ey = 2;
                break;
            case 6:
                var20 = this.Family;
                var21 = 72;
                ex = 3;
                ey = 3;
                break;
            case 7:
                var20 = 4;
                var21 = 96;
                ex = 3;
                ey = 3;
                break;
            default:
                var20 = this.Family;
                var21 = 24;
        }

        CPCLCmd = JniUtils.drawText4() + ex + " " + ey;
        this.portSendCmd(CPCLCmd);
        char[] array = str.toCharArray();
        str = "";

        for (int i = 0; i < array.length; ++i) {
            if ((char) ((byte) array[i]) != array[i]) {
                Width += var21;
                if (Width > width) {
                    switch (rotate) {
                        case 1:
                            if (reverse) {
                                CPCLCmd = JniUtils.drawText6() + var20 + " " + size + " " + text_x + " " + text_y + " " + str;
                                this.portSendCmd(CPCLCmd);
                            } else {
                                CPCLCmd = JniUtils.drawText10() + var20 + " " + size + " " + text_x + " " + text_y + " " + str;
                                this.portSendCmd(CPCLCmd);
                            }
                            break;
                        case 2:
                            if (reverse) {
                                CPCLCmd = JniUtils.drawText7() + var20 + " " + size + " " + text_x + " " + text_y + " " + str;
                                this.portSendCmd(CPCLCmd);
                            } else {
                                CPCLCmd = JniUtils.drawText11() + var20 + " " + size + " " + text_x + " " + text_y + " " + str;
                                this.portSendCmd(CPCLCmd);
                            }
                            break;
                        case 3:
                            if (reverse) {
                                CPCLCmd = JniUtils.drawText8() + var20 + " " + size + " " + text_x + " " + text_y + " " + str;
                                this.portSendCmd(CPCLCmd);
                            } else {
                                CPCLCmd = JniUtils.drawText12() + var20 + " " + size + " " + text_x + " " + text_y + " " + str;
                                this.portSendCmd(CPCLCmd);
                            }
                            break;
                        default:
                            if (reverse) {
                                CPCLCmd = JniUtils.drawText5() + var20 + " " + size + " " + text_x + " " + text_y + " " + str;
                                this.portSendCmd(CPCLCmd);
                            } else {
                                CPCLCmd = JniUtils.drawText9() + var20 + " " + size + " " + text_x + " " + text_y + " " + str;
                                this.portSendCmd(CPCLCmd);
                            }
                    }

                    text_y += var21;
                    Width = var21;
                    str = String.valueOf(array[i]);
                } else {
                    str = str + String.valueOf(array[i]);
                }
            } else {
                Width += var21 / 2;
                if (Width > width) {
                    switch (rotate) {
                        case 1:
                            if (reverse) {
                                CPCLCmd = JniUtils.drawText6() + var20 + " " + size + " " + text_x + " " + text_y + " " + str;
                                this.portSendCmd(CPCLCmd);
                            } else {
                                CPCLCmd = JniUtils.drawText10() + var20 + " " + size + " " + text_x + " " + text_y + " " + str;
                                this.portSendCmd(CPCLCmd);
                            }
                            break;
                        case 2:
                            if (reverse) {
                                CPCLCmd = JniUtils.drawText7() + var20 + " " + size + " " + text_x + " " + text_y + " " + str;
                                this.portSendCmd(CPCLCmd);
                            } else {
                                CPCLCmd = JniUtils.drawText11() + var20 + " " + size + " " + text_x + " " + text_y + " " + str;
                                this.portSendCmd(CPCLCmd);
                            }
                            break;
                        case 3:
                            if (reverse) {
                                CPCLCmd = JniUtils.drawText8() + var20 + " " + size + " " + text_x + " " + text_y + " " + str;
                                this.portSendCmd(CPCLCmd);
                            } else {
                                CPCLCmd = JniUtils.drawText12() + var20 + " " + size + " " + text_x + " " + text_y + " " + str;
                                this.portSendCmd(CPCLCmd);
                            }
                            break;
                        default:
                            if (reverse) {
                                CPCLCmd = JniUtils.drawText5() + var20 + " " + size + " " + text_x + " " + text_y + " " + str;
                                this.portSendCmd(CPCLCmd);
                            } else {
                                CPCLCmd = JniUtils.drawText9() + var20 + " " + size + " " + text_x + " " + text_y + " " + str;
                                this.portSendCmd(CPCLCmd);
                            }
                    }

                    text_y += var21;
                    Width = var21 / 2;
                    str = String.valueOf(array[i]);
                } else {
                    str = str + String.valueOf(array[i]);
                }
            }
        }

        switch (rotate) {
            case 1:
                if (reverse) {
                    CPCLCmd = JniUtils.drawText6() + var20 + " " + size + " " + text_x + " " + text_y + " " + str;
                    this.portSendCmd(CPCLCmd);
                } else {
                    CPCLCmd = JniUtils.drawText10() + var20 + " " + size + " " + text_x + " " + text_y + " " + str;
                    this.portSendCmd(CPCLCmd);
                }
                break;
            case 2:
                if (reverse) {
                    CPCLCmd = JniUtils.drawText7() + var20 + " " + size + " " + text_x + " " + text_y + " " + str;
                    this.portSendCmd(CPCLCmd);
                } else {
                    CPCLCmd = JniUtils.drawText11() + var20 + " " + size + " " + text_x + " " + text_y + " " + str;
                    this.portSendCmd(CPCLCmd);
                }
                break;
            case 3:
                if (reverse) {
                    CPCLCmd = JniUtils.drawText8() + var20 + " " + size + " " + text_x + " " + text_y + " " + str;
                    this.portSendCmd(CPCLCmd);
                } else {
                    CPCLCmd = JniUtils.drawText12() + var20 + " " + size + " " + text_x + " " + text_y + " " + str;
                    this.portSendCmd(CPCLCmd);
                }
                break;
            default:
                if (reverse) {
                    CPCLCmd = JniUtils.drawText5() + var20 + " " + size + " " + text_x + " " + text_y + " " + str;
                    this.portSendCmd(CPCLCmd);
                } else {
                    CPCLCmd = JniUtils.drawText9() + var20 + " " + size + " " + text_x + " " + text_y + " " + str;
                    this.portSendCmd(CPCLCmd);
                }
        }

    }

    public void drawBarCode(int start_x, int start_y, String text, int type, int rotate, int linewidth, int height) {
        byte radio = 2;
        String CPCLCmd = "";
        if (rotate != 0) {
            CPCLCmd = JniUtils.drawBarCode1() + (linewidth - 1) + " " + radio + " " + height + " " + start_x + " " + start_y + " " + text;
        } else {
            CPCLCmd = JniUtils.drawBarCode2() + (linewidth - 1) + " " + radio + " " + height + " " + start_x + " " + start_y + " " + text;
        }

        this.portSendCmd(CPCLCmd);
    }

    private int barcodeWidth(String text, BarcodeFormat format, int Width, int Height) throws WriterException {
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix temp = writer.encode(text, format, 1, 1);
        BitMatrix result = writer.encode(text, format, temp.getWidth() * Width, Height);
        int width = result.getWidth();
        int height = result.getHeight();
        int AAA = 0;
        int BBB = 0;

        int x;
        for (x = 0; x < width; ++x) {
            if (result.get(x, 0)) {
                AAA = x;
                break;
            }
        }

        for (x = 0; x < width; ++x) {
            if (result.get(width - 1 - x, 0)) {
                BBB = x;
                break;
            }
        }

        width -= AAA + BBB;
        return width;
    }

    public void drawBarCode(int start_x, int start_y, int end_x, int end_y, String text, int type, int rotate, int linewidth, int height, int direction) {
        byte radio = 2;
        String CPCLCmd = "";
        BarcodeFormat barcodeFormat;
        switch (type) {
            case 0:
                barcodeFormat = BarcodeFormat.CODE_39;
                break;
            case 1:
                barcodeFormat = BarcodeFormat.CODE_128;
                break;
            case 2:
                barcodeFormat = BarcodeFormat.CODE_93;
                break;
            case 3:
                barcodeFormat = BarcodeFormat.CODABAR;
                break;
            case 4:
                barcodeFormat = BarcodeFormat.EAN_8;
                break;
            case 5:
                barcodeFormat = BarcodeFormat.EAN_13;
                break;
            case 6:
                barcodeFormat = BarcodeFormat.UPC_A;
                break;
            case 7:
                barcodeFormat = BarcodeFormat.UPC_E;
                break;
            case 8:
                barcodeFormat = BarcodeFormat.ITF;
                break;
            default:
                barcodeFormat = BarcodeFormat.CODE_128;
        }

        try {
            int e = this.barcodeWidth(text, barcodeFormat, linewidth, height);
            int MaxWidth = end_x - start_x;
            int MaxHeight = end_y - start_y;
            switch (direction) {
                case 1:
                    if (rotate == 0) {
                        if (e < MaxWidth) {
                            start_x += (MaxWidth - e) / 2;
                        }
                    } else if (e < MaxHeight) {
                        start_y += (MaxHeight - e) / 2;
                    }
                    break;
                case 2:
                    if (rotate == 0) {
                        if (e < MaxWidth) {
                            start_x += MaxWidth - e;
                        }
                    } else if (e < MaxWidth) {
                        start_y += MaxHeight - e;
                    }
            }
        } catch (WriterException var17) {
            var17.printStackTrace();
        }

        if (rotate != 0) {
            CPCLCmd = JniUtils.drawBarCode1() + (linewidth - 1) + " " + radio + " " + height + " " + start_x + " " + start_y + " " + text;
        } else {
            CPCLCmd = JniUtils.drawBarCode2() + (linewidth - 1) + " " + radio + " " + height + " " + start_x + " " + start_y + " " + text;
        }

        this.portSendCmd(CPCLCmd);
    }

    public void drawQrCode(int start_x, int start_y, String text, int rotate, int ver, int lel) {
        String CPCLCmd = JniUtils.drawQrCode1() + ver;
        this.portSendCmd(CPCLCmd);
        CPCLCmd = JniUtils.drawQrCode2() + start_x + " " + start_y + " M 2 " + "U " + lel + "\r\nMA," + text + "\r\nENDQR";
        this.portSendCmd(CPCLCmd);
    }

    private String printHexString(byte[] b) {
        String a = "";

        for (int i = 0; i < b.length; ++i) {
            String hex = Integer.toHexString(b[i] & 255);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }

            a = a + hex;
        }

        return a;
    }

    public void drawGraphic(int start_x, int start_y, int bmp_size_x, int bmp_size_y, Bitmap bmp) {
        int ByteWidth = (bmp_size_x - 1) / 8 + 1;
        int ByteHeight = bmp_size_y;
        String Data = "";
        byte[] DataByte = new byte[ByteWidth * bmp_size_y];
        int offset = 0;
        int cur_idx = 0;
        int last_idx = 0;
        int i = 0;

        while (true) {
            do {
                if (i >= bmp_size_y) {
                    return;
                }

                for (int CPCLCmd = 0; CPCLCmd < bmp_size_x; ++CPCLCmd) {
                    cur_idx = i * ByteWidth + CPCLCmd / 8;
                    if ((bmp.getPixel(CPCLCmd, i) & 16777215) < 3092271) {
                        DataByte[cur_idx] = (byte) (DataByte[cur_idx] | 128 >> CPCLCmd % 8);
                    } else {
                        DataByte[cur_idx] = (byte) (DataByte[cur_idx] & ~(128 >> CPCLCmd % 8));
                    }
                }

                ++i;
            } while ((i - offset) * ByteWidth < 1024 && i != ByteHeight);

            Data = this.printHexString(this.getByte(DataByte, last_idx, cur_idx - last_idx + 1));
            last_idx = cur_idx + 1;
            String var15 = JniUtils.drawGraphic() + ByteWidth + " " + (i - offset) + " " + start_x + " " + (start_y + offset) + " " + Data;
            this.portSendCmd(var15);
            offset = i;
        }
    }

    public void drawGraphic2(int start_x, int start_y, int bmp_size_x, int bmp_size_y, Bitmap bmp) {
        int ByteWidth = (bmp_size_x - 1) / 8 + 1;
        String Data = "";
        byte[] DataByte = new byte[ByteWidth * bmp_size_y];

        for (int CPCLCmd = 0; CPCLCmd < bmp_size_y; ++CPCLCmd) {
            for (int j = 0; j < bmp_size_x; ++j) {
                if (bmp.getPixel(j, CPCLCmd) == Color.BLACK) {
                    DataByte[CPCLCmd * ByteWidth + j / 8] = (byte) (DataByte[CPCLCmd * ByteWidth + j / 8] | 128 >> j % 8);
                }
            }
        }

        Data = this.printHexString(DataByte);
        String var12 = JniUtils.drawGraphic() + ByteWidth + " " + bmp_size_y + " " + start_x + " " + start_y + " " + Data;
        this.portSendCmd(var12);
    }

    private byte[] getByte(byte[] dataByte, int start, int length) {
        byte[] newDataByte = new byte[length];

        for (int i = 0; i < length; ++i) {
            newDataByte[i] = dataByte[start + i];
        }

        return newDataByte;
    }

    public String printerStatus() {
        if (this.Port.isOpen) {
            byte[] Cmd = JniUtils.printerStatus();
            byte[] Rep = new byte[2];
            this.Port.flushReadBuffer();
            return !this.Port.write(Cmd, 0, 3) ? "Print Write Error" : (!this.Port.read(Rep, 2, 3000) ? "Print Read Error" : ((Rep[0] & 16) != 0 ? "CoverOpened" : ((Rep[0] & 1) != 0 ? "NoPaper" : ((Rep[0] & 2) != 0 ? "Overheat" : ((Rep[0] & 8) != 0 ? "Printing" : ((Rep[0] & 4) != 0 ? "BatteryLow" : "OK"))))));
        } else {
            return "Printer is disconnect";
        }
    }

    public String printerType() {
        return "QR";
    }

    public void feed() {
        if (this.Port.isOpen) {
            byte[] cmd = new byte[]{JniUtils.feed()[0]};
            this.Port.write(cmd, 0, 1);
        }

    }

    public void setPaperFeedLength(int mm) {
        byte[] cmd = JniUtils.setPaperFeedLength();
        mm *= 8;
        cmd[2] = (byte) (mm & 255);
        cmd[3] = (byte) (mm >> 8 & 255);
        this.Port.write(cmd, 0, 4);
    }

    public void shutdown() {
        byte[] cmd = new byte[]{(byte) 30, (byte) 3, (byte) 8, (byte) 0, (byte) -1, (byte) -3, (byte) 1, (byte) 16, (byte) 76, (byte) -11, (byte) -106, (byte) -49};
        this.Port.write(cmd, 0, 12);
    }

    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    public boolean checkID(String key) {
        if (key != null && key.length() > 0) {
            boolean result = false;
            if (this.Port.isOpen) {
                byte[] Rep = new byte[3];
                this.Port.flushReadBuffer();
                byte[] cmdJni = JniUtils.checkID1();
                byte[] byte1 = new byte[]{cmdJni[0], cmdJni[1], (byte) key.getBytes().length};
                byte[] byte2 = key.getBytes();
                byte[] cmd = byteMerger(byte1, byte2);
                boolean writeResult = this.Port.write(cmd, 0, cmd.length);
                if (writeResult && this.Port.read(Rep, 3, 3000) && Rep[2] == 0) {
                    result = true;
                }
            }

            return result;
        } else {
            return false;
        }
    }

    public byte[] checkID2(String key) {
        byte[] Rep = new byte[]{(byte) 0, (byte) 0, (byte) -1};
        if (this.Port.isOpen) {
            this.Port.flushReadBuffer();
            byte[] cmdJni = JniUtils.checkID1();
            byte[] byte1 = new byte[]{cmdJni[0], cmdJni[1], (byte) key.getBytes().length};
            byte[] byte2 = key.getBytes();
            byte[] cmd = byteMerger(byte1, byte2);
            boolean writeResult = this.Port.write(cmd, 0, cmd.length);
            if (writeResult) {
                this.Port.read(Rep, 3, 3000);
            }
        }

        return Rep;
    }

    public boolean disableID() {
        boolean result = false;
        if (this.Port.isOpen) {
            byte[] Rep = new byte[3];
            this.Port.flushReadBuffer();
            byte[] cmd = JniUtils.checkID2();
            if (this.Port.write(cmd, 0, 2) && this.Port.read(Rep, 3, 3000) && Rep[2] == 0) {
                result = true;
            }
        }

        return result;
    }

    public byte[] disableID2() {
        byte[] Rep = new byte[3];
        if (this.Port.isOpen) {
            this.Port.flushReadBuffer();
            byte[] cmd = JniUtils.checkID2();
            boolean writeResult = this.Port.write(cmd, 0, 2);
            if (writeResult) {
                this.Port.read(Rep, 3, 3000);
            }
        }

        return Rep;
    }

    public byte[] readIdcard2() {
        byte[] Rep = (byte[]) null;
        if (this.Port.isOpen) {
            byte[] cmd = new byte[2];
            cmd[0] = (byte) 0x1d;
            cmd[1] = (byte) 0xfe;
            int readLength;
            if (this.Port.write(cmd, 0,2)) {
                Log.e("Log","开始读取了");
                readLength = this.Port.readLength();
                Rep = new byte[readLength];
                this.Port.read(Rep, readLength, 1000);
            }
            return Rep;
        }
        return null;
    }

    public byte[] readIdcard() {
        if (this.Port.isOpen) {
            byte[] cmd = JniUtils.sendIdCardCommand2();
            byte[] Rep = this.sendIdCardCommand(new byte[]{(byte) 0x1D, (byte) 0xFF, (byte) 0x0A, (byte) 0x00, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, (byte) 0x69, (byte) 0x00, (byte) 0x03, (byte) 0x20, (byte) 0x01, (byte) 0x22});
//            byte[] Rep = this.sendIdCardCommand(cmd);
            if (Rep != null && Rep.length > 10 && Rep[6] != 4 && Rep[9] != 128) {
                cmd = JniUtils.sendIdCardCommand3();
//                Rep = this.sendIdCardCommand(cmd);
                Rep = this.sendIdCardCommand(new byte[]{(byte) 0x1D, (byte) 0xFF, (byte) 0x0A, (byte) 0x00, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, (byte) 0x69, (byte) 0x00, (byte) 0x03, (byte) 0x20, (byte) 0x02, (byte) 0x21, (byte) 0x00});
                if (Rep != null && Rep.length > 10 && Rep[6] != 4 && Rep[9] != 129) {
                    cmd = JniUtils.sendIdCardCommand4();
//                    Rep = this.sendIdCardCommand(cmd);
                    Rep = this.sendIdCardCommand(new byte[]{(byte) 0x1D, (byte) 0xFF, (byte) 0x0A, (byte) 0x00, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, (byte) 0x69, (byte) 0x00, (byte) 0x03, (byte) 0x30, (byte) 0x01, (byte) 0x32});
                    if (Rep != null && Rep.length > 10 && Rep[6] != 4 && Rep[9] != 65 && Rep[10] != 69) {
                        return Rep;
                    }
                }
            }
        }
        return null;
    }


    public byte[] sendIdCardCommand(byte[] command) {
        byte[] Rep = (byte[]) null;
        int length = command.length;
        byte[] cmd = new byte[4 + length];
        byte[] cmdJni = JniUtils.sendIdCardCommand1();
        cmd[0] = cmdJni[0];
        cmd[1] = cmdJni[1];
        cmd[2] = (byte) (length & 255);
        cmd[3] = (byte) (length >> 8 & 255);

        int readLength;
        for (readLength = 0; readLength < length; ++readLength) {
            cmd[readLength + 4] = command[readLength];
        }

        if (this.Port.write(cmd, 0, 4 + length)) {
            readLength = this.Port.readLength();
            Rep = new byte[readLength];
            this.Port.read(Rep, readLength, 3000);
        }
        return Rep;
    }
}
