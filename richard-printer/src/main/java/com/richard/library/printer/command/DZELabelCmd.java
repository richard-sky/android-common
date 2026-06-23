package com.richard.library.printer.command;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.regex.Pattern;

/**
 * author：Richard
 * time：2021-11-23 17:25
 * version：v1.0.0
 * description：价签打印指令集  Direct Protoco（DP），ZSim（ZPL），ESim（EPL）指令集
 */
public class DZELabelCmd {

    private final String TAG = "labelCmd";
    private final String default_font = "MHeiGB18030C-Medium";
    private final _HoneywellPrintConfig m_printConfig = new _HoneywellPrintConfig();
    private byte[] m_byteToPrint = "".getBytes();
    static String[] g_szMediaTypes = new String[]{"Black Mark", "Media With Gaps", "Continuous Fix Len", "Continuous Var Len"};
    static String[] g_szPrintMethods = new String[]{"No Ribbon (DT)", "Ribbon (TTR)"};
    static String[] g_szCalibrateModes = new String[]{"Slow", "Fast"};
    static String[] g_szPowerUpActions = new String[]{"No Action", "FormFeed", "TestFeed", "Smart Calibration"};
    static String[] g_szHeadDownActions = new String[]{"No Action", "FormFeed", "TestFeed", "Smart Calibration"};
    static String g_szPrnCmd_Pos = "PP";
    static String g_szPrnCmd_Text = "PT";
    static String g_szPrnCmd_Prbuf = "PRBUF";
    static String g_szPrnCmd_Feed = "PF";
    static String g_szPrnCmd_Enter = "\n";
    static String g_szPrnCmd_Font = "FT";
    static String g_szPrnCmd_Dir = "DIR";
    static String g_szPrnCmd_Align = "AN";
    static String g_szPrnCmd_BarType = "BT";
    static String g_szPrnCmd_BarFont = "BF";
    static String g_szPrnCmd_Barhg = "BH";
    static String g_szPrnCmd_BarMng = "BM";
    static String g_szPrnCmd_Prbar = "PB";
    static String g_szPrnCmd_Barset = "BARSET";
    static String g_szSetupMediaType = "SETUP \"Printing,Media,Media Type,%s\"";
    static String g_szSetupPrintMethod = "SETUP \"Printing,Media,Print Method,%s\"";
    static String g_szSetupMediaMarginX = "SETUP \"Printing,Media,Print Area,Media Margin (X),%d\"";
    static String g_szSetupMediaWidth = "SETUP \"Printing,Media,Print Area,Media Width,%d\"";
    static String g_szSetupMediaLength = "SETUP \"Printing,Media,Print Area,Media Length,%d\"";
    static String g_szSetupTopAdjust = "SETUP \"Printing,Media,Label Top Adjust,%d\"";
    static String g_szSetupRestAdjust = "SETUP \"Printing,Media,Label Rest Adjust,%d\"";
    static String g_szSetupMediaCalibrateMode = "SETUP \"Printing,Media,Media Calibration Mode,%s\"";
    static String g_szSetupPowerUpAction = "SETUP \"Printing,Media,Action,Power Up Action,%s\"";
    static String g_szSetupHeadDownAction = "SETUP \"Printing,Media,Action,Head Down Action,%s\"";
    static String g_szSetupPrintSpeed = "SETUP \"Printing,Print Quality,Print Speed,%d\"";
    static String g_szSetupDarkness = "SETUP \"Printing,Print Quality,Darkness,%d\"";

    /**
     * 获取最终指令结果
     */
    public byte[] getCmdResult() {
        return this.m_byteToPrint;
    }

    /**
     * 追加打印线条
     *
     * @param posX       x坐标
     * @param posY       y坐标
     * @param lineLength 线条大小
     * @param lineWidth  线条宽度
     * @param direction        方向
     * @param align      对齐方式
     */
    public void PRN_AddLine(int posX, int posY, int lineLength, int lineWidth, PRN_DIRECTION direction, PRN_ALIGNMENT align) {
        try {
            byte[] byteTemp = ("DIR " + direction.value() + ":AN " + align.value() + ":PP " + posX + "," + posY + ":PL " + lineLength + "," + lineWidth + "\n").getBytes();
            this.m_byteToPrint = this.ByteMerger(this.m_byteToPrint, byteTemp);
        } catch (Exception var8) {
            var8.printStackTrace();
            this.m_byteToPrint = null;
        }
    }

    /**
     * 追加打印的文本
     *
     * @param textToPrint 文本字符串
     * @param posX        打印起点X坐标
     * @param posY        打印起点Y坐标
     */
    public void PRN_AddTextToLabel(String textToPrint, int posX, int posY) {
        textToPrint = textToPrint.replace("\"", "\"+CHR$(34)+\"");
        textToPrint = textToPrint.replace("\r\n", "\"+CHR$(10)+\"");
        textToPrint = textToPrint.replace("\r", "\"+CHR$(10)+\"");
        textToPrint = textToPrint.replace("\n", "\"+CHR$(10)+\"");
        textToPrint = "\"" + textToPrint + "\"";

        try {
            byte[] byteToPrint;
            byte[] byteTemp;
            if (this.IsChinese(textToPrint)) {
                byteTemp = "NASC 936\n".getBytes();
                byteToPrint = textToPrint.getBytes("GBK");
            } else {
                byteTemp = "".getBytes();
                byteToPrint = textToPrint.getBytes();
            }

            byteTemp = this.ByteMerger(byteTemp, ("PP " + posX + "," + posY + "\n").getBytes());
            byteTemp = this.ByteMerger(byteTemp, "PT ".getBytes());
            byteTemp = this.ByteMerger(byteTemp, byteToPrint);
            byteTemp = this.ByteMerger(byteTemp, "\n".getBytes());
            this.m_byteToPrint = this.ByteMerger(this.m_byteToPrint, byteTemp);
        } catch (Exception var6) {
            var6.printStackTrace();
        }
    }

    /**
     * 追加需打印的文本
     *
     * @param textToPrint 需打印的文本字符串
     * @param fontName    文本字体名
     * @param fontSize    文本字体大小
     * @param posX        打印起点X坐标
     * @param posY        打印起点Y坐标
     * @param direction         文本的方向. 请参照PRN_DIRECTION的定义.
     * @param align       文本的对齐基准点. 请参照PRN_ ALIGNMENT的定义
     */
    public void PRN_AddTextToLabelEx(String textToPrint, String fontName, int fontSize, int posX, int posY, PRN_DIRECTION direction, PRN_ALIGNMENT align) {
        textToPrint = textToPrint.replace("\"", "\"+CHR$(34)+\"");
        textToPrint = "\"" + textToPrint + "\"";
        fontName = TextUtils.isEmpty(fontName) ? default_font : fontName;

        try {
            byte[] byteToPrint;
            byte[] byteTemp;
            if (this.IsChinese(textToPrint)) {
                byteTemp = "NASC 936:".getBytes();
                byteToPrint = textToPrint.getBytes("GBK");
            } else {
                byteTemp = "".getBytes();
                byteToPrint = textToPrint.getBytes();
            }

            byteTemp = this.ByteMerger(byteTemp, ("FT \"" + fontName + "\"," + fontSize + ":PP " + posX + "," + posY + ":DIR " + direction.value() + ":AN " + align.value() + ":PT ").getBytes());
            byteTemp = this.ByteMerger(byteTemp, byteToPrint);
            byteTemp = this.ByteMerger(byteTemp, "\n".getBytes());
            this.m_byteToPrint = this.ByteMerger(this.m_byteToPrint, byteTemp);
        } catch (Exception var10) {
            var10.printStackTrace();
        }
    }

    /**
     * 设定条码解释文本的字体
     *
     * @param fontName 条码解释文本的字体，如果设定为空，或字体内容设定为空，则不显示条码解释文本
     * @param size     条码解释文本的高度
     * @param slant    条码解释文本的倾斜角度(0-90)
     * @param offset   条码解释文本与条码间的间隔点数
     */
    public void PRN_IndBarcodeFont(String fontName, int size, int slant, int offset) {
        if (null != fontName && fontName.length() != 0) {
            if (offset < 0 || slant > 90 || slant < 0 || size <= 0) {
                Log.e(TAG, "----PRN_IndBarcodeFont()---参数无效");
                return;
            }

            this.m_byteToPrint = this.ByteMerger(this.m_byteToPrint, ("BF \"" + fontName + "\"," + size + "," + slant + "," + offset + ",1,1,100 ON\n").getBytes());
        } else {
            this.m_byteToPrint = this.ByteMerger(this.m_byteToPrint, "BF OFF\n".getBytes());
        }
    }

    /**
     * 追加需打印的条码
     *
     * @param codeToPrint  需打印的条码字符串
     * @param codeType     条码类型
     * @param height       条码高度.打印二维码时，建议设定为2.
     * @param posX         打印起点X坐标
     * @param posY         打印起点Y坐标
     * @param direction    条码的方向. 请参照PRN_DIRECTION的定义
     * @param align        条码的对齐基准点. 请参照PRN_ ALIGNMENT的定义.如设定为2，条码的解释文本会居中显示。
     * @param enlargeWidth 文本的放大倍数. 打印一维码时，放大宽度。打印二维码时，宽度和高度都会放大
     */
    public void PRN_AddBarcodeToLabel(String codeToPrint, String codeType, int height, int posX, int posY, PRN_DIRECTION direction, PRN_ALIGNMENT align, int enlargeWidth) {
        codeToPrint = codeToPrint.replace("\"", "\"+CHR$(34)+\"");
        codeToPrint = "\"" + codeToPrint + "\"";
        codeType = "\"" + codeType + "\"";
        byte[] byteTemp = ("DIR " + direction.value() + ":AN " + align.value() + ":BT " + codeType + ":BH " + height + ":BM " + enlargeWidth + ":PP " + posX + "," + posY + ":PB " + codeToPrint + "\n").getBytes();
        this.m_byteToPrint = this.ByteMerger(this.m_byteToPrint, byteTemp);
    }

    /**
     * 追加需打印的条码
     *
     * @param codeToPrint  需打印的条码字符串
     * @param codeType     条码类型
     * @param height       条码高度.打印二维码时，建议设定为2
     * @param posX         打印起点X坐标
     * @param posY         打印起点Y坐标
     * @param direction          条码方向. 请参照PRN_DIRECTION定义
     * @param align        条码的对齐基准点. 请参照PRN_ALIGNMENT的定义。如设定为2，条码的解释文本会居中显示
     * @param enlargeWidth 文本的放大倍数。打印一维码时，放大宽度。打印二维码时，宽度和高度都会放大
     * @param piAdv        可选参数，用于设置条码特殊属性。
     *                     该参数为数组指针，指向长度为12的int数组。
     *                     若该参数为NULL，则用默认值来设置条码特殊属性。
     *                     若 codeType 为“QRCODE”，该参数作用如下：
     *                     adv [0] 为 纠错级别。默认为2。
     *                     adv [1] 为 模式。默认为1，推荐为2。 若 codeType 为“PDF417”，, 该参数作用如下：
     *                     adv [0] 为 纠错级别。默认为2。
     *                     adv [1] 为 二维码行数。默认为0。
     *                     adv [2] 为 二维码列数。默认为0。 若 codeType 为其他类型，该参数作用如下:
     *                     adv [0] 为 Ratio of the large bars。默认为3。
     *                     adv [1] 为 Ratio of the small bars。默认为1。
     */
    public void PRN_AddBarcodeToLabelEx(String codeToPrint, String codeType, int height, int posX, int posY, PRN_DIRECTION direction, PRN_ALIGNMENT align, int enlargeWidth, int[] piAdv) {
        int iLightBarRatio = 3;
        int iNarrowBarRatio = 1;
        int iBarheight = height;
        int iSecLev = 2;
        int iAspectHeightRatio = 3;
        int iAspectWidthRatio = 1;
        int iRowsNumber = 0;
        int iColumnsNumber = 0;
        int iTruncate = 0;

        if (!(codeToPrint != null && codeToPrint.length() != 0 && codeType != null && codeType.length() != 0 && height > 0 && enlargeWidth > 0)) {
            Log.e(TAG, "----PRN_AddBarcodeToLabelEx()---参数无效");
            return;
        }

        if (codeType.equals("QRCODE")) {
            if (null != piAdv) {
                iSecLev = piAdv[0];
                iBarheight = piAdv[1];
            } else {
                iBarheight = 2;
            }

            if (iBarheight < 1 || iBarheight > 2 || enlargeWidth < 1 || enlargeWidth > 27 || iSecLev < 1 || iSecLev > 4) {
                Log.e(TAG, "----PRN_AddBarcodeToLabelEx()---参数无效");
                return;
            }
        } else if (codeType.equals("PDF417")) {
            if (null != piAdv) {
                iSecLev = piAdv[0];
                iRowsNumber = piAdv[1];
                iColumnsNumber = piAdv[2];
            }

            if (enlargeWidth >= 128 || height >= 500 || iSecLev < 1 || iSecLev > 5) {
                Log.e(TAG, "----PRN_AddBarcodeToLabelEx()---参数无效");
                return;
            }
        } else if (null != piAdv) {
            iLightBarRatio = piAdv[0];
            iNarrowBarRatio = piAdv[1];
        }

        String szBarsetBuf = String.format("\"%s\", %d, %d, %d, %d, %d, %d, %d, %d, %d, %d", codeType,
                iLightBarRatio, iNarrowBarRatio, enlargeWidth, iBarheight, iSecLev, Integer.valueOf(iAspectHeightRatio),
                Integer.valueOf(iAspectWidthRatio), iRowsNumber, iColumnsNumber, Integer.valueOf(iTruncate));
        codeToPrint = codeToPrint.replace("\"", "\"+CHR$(34)+\"");
        codeToPrint = "\"" + codeToPrint + "\"";
        String pszCmdBuffer = String.format("%s %s:%s %d:%s %d:%s %d,%d:%s %s\n", g_szPrnCmd_Barset,
                szBarsetBuf, g_szPrnCmd_Dir, direction.value(), g_szPrnCmd_Align, align.value(), g_szPrnCmd_Pos,
                posX, posY, g_szPrnCmd_Prbar, codeToPrint);
        this.m_byteToPrint = this.ByteMerger(this.m_byteToPrint, pszCmdBuffer.getBytes());
    }

    /**
     * bitmap转byte
     */
    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * bitmap转byte
     */
    public byte[] bitmapToBytes(Bitmap bitmap) {
        int bytes = bitmap.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(bytes);
        bitmap.copyPixelsToBuffer(buffer);
        byte[] data = buffer.array();
        return data;
    }

    /**
     * 排列bitmap
     */
    public byte[] formateBmg(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        byte[] pixels = new byte[w * h];

        for (int i = 0; i < h; ++i) {
            for (int j = 0; j < w; ++j) {
                pixels[i * w + j] = (byte) bitmap.getPixel(i, j);
            }
        }

        return pixels;
    }

    /**
     * 追加需打印的图片.图片必须是黑白无灰度的图片，格式支持PNG, GIF, BMP, 及 PCX. 你应该在调用PRN_PrintLabel 前追加所有需打印的内容.
     * 另外你需要设定其它打印参数，如打印起点的坐标，该坐标和打印头的像素有关，单位是像素
     *
     * @param imageBuf 图片文件的路径
     * @param posX     打印起点X坐标
     * @param posY     打印起点Y坐标
     */
    public void PRN_AddImageToLabel(byte[] imageBuf, int posX, int posY) {
        try {
            int iSize = imageBuf.length;
            if (iSize <= 0) {
                Log.e(TAG, "---PRN_AddImageToLabel()--参数无效");
                return;
            }

            byte[] byteTemp = ("PP " + posX + "," + posY + ":PRBUF " + iSize + "\n").getBytes();
            byteTemp = this.ByteMerger(byteTemp, imageBuf);
            this.m_byteToPrint = this.ByteMerger(this.m_byteToPrint, byteTemp);
            this.m_byteToPrint = this.ByteMerger(this.m_byteToPrint, "\n".getBytes());
        } catch (Exception var6) {
            var6.printStackTrace();
        }
    }

    /**
     * 向右旋转
     */
    RectF rightRotate(RectF rect, float x, float y) {
        RectF newRect = new RectF();
        newRect.top = -rect.left + x + y;
        newRect.bottom = -rect.right + x + y;
        newRect.left = -y + rect.bottom + x;
        newRect.right = -y + rect.top + x;
        return newRect;
    }

    /**
     * 旋转
     *
     * @param rect
     * @param direction   对齐方向
     * @param align 对齐方式
     */
    RectF rotate(RectF rect, PRN_DIRECTION direction, PRN_ALIGNMENT align) {
        float x = 0.0F;
        float y = 0.0F;
        switch (align) {
            case PRN_ANCHOR_1:
                x = rect.left;
                y = rect.bottom;
                break;
            case PRN_ANCHOR_2:
                x = rect.left + rect.width() / 2.0F;
                y = rect.bottom;
                break;
            case PRN_ANCHOR_3:
                x = rect.right;
                y = rect.bottom;
                break;
            case PRN_ANCHOR_4:
                x = rect.left;
                y = rect.bottom - rect.height() / 2.0F;
                break;
            case PRN_ANCHOR_5:
                x = rect.left + rect.width() / 2.0F;
                y = rect.bottom - rect.height() / 2.0F;
                break;
            case PRN_ANCHOR_6:
                x = rect.right;
                y = rect.bottom - rect.height() / 2.0F;
                break;
            case PRN_ANCHOR_7:
                x = rect.left;
                y = rect.top;
                break;
            case PRN_ANCHOR_8:
                x = rect.left + rect.width() / 2.0F;
                y = rect.right;
                break;
            case PRN_ANCHOR_9:
                x = rect.left;
                y = rect.top;
                break;
            default:
                return null;
        }

        RectF result = new RectF();
        result.left = rect.left;
        result.right = rect.right;
        result.top = rect.top;
        result.bottom = rect.bottom;
        switch (direction) {
            case PRN_DIRECTION_1:
                return result;
            case PRN_DIRECTION_2:
                result = this.rightRotate(result, x, y);
                return result;
            case PRN_DIRECTION_3:
                result = this.rightRotate(result, x, y);
                result = this.rightRotate(result, x, y);
                return result;
            case PRN_DIRECTION_4:
                result = this.rightRotate(result, x, y);
                result = this.rightRotate(result, x, y);
                result = this.rightRotate(result, x, y);
                return result;
            default:
                return null;
        }
    }

    /**
     * 追加圆形到标签
     */
    public void PRN_AddRoundRectToLabel(int posX, int posY, int width, int height, int thickness, int radius, PRN_DIRECTION direction, PRN_ALIGNMENT align) {
        if (!(radius * 2 + thickness <= width && radius * 2 + thickness <= height)) {
            Log.e(TAG, "--PRN_AddRoundRectToLabel()--参数无效--");
            return;
        }

        RectF rectRotate = new RectF((float) posX, (float) (posY + height), (float) (posX + width), (float) posY);
        rectRotate = this.rotate(rectRotate, direction, align);
        int offset = (thickness + 9) / 10 * 10;
        Bitmap bitmap = Bitmap.createBitmap(offset + (int) rectRotate.right, offset + (int) rectRotate.top, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(-16777216);
        paint.setStrokeWidth((float) thickness);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawColor(-1);
        RectF rect = new RectF(rectRotate.left, (float) offset, rectRotate.right, (float) offset + Math.abs(rectRotate.height()));
        canvas.drawRoundRect(rect, (float) radius, (float) radius, paint);

        try {
            File imgFile = File.createTempFile("temp", ".png");
            OutputStream os = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            InputStream inputStream = new FileInputStream(imgFile);
            byte[] buf = this.getFileByte(inputStream);
            inputStream.close();
            this.PRN_AddImageToLabel(buf, 0, 0);
        } catch (FileNotFoundException var19) {
            var19.printStackTrace();
        } catch (IOException var20) {
            var20.printStackTrace();
        }
    }


    /**
     * 追加需打印的图片.图片必须是黑白无灰度的图片，格式支持PNG, GIF,
     * BMP, 及 PCX. 你应该在调用PRN_PrintLabel 前追加所有需打印的内容.
     * 另外你需要设定其它打印参数，如打印起点的坐标，该坐标和打印头的像
     * 素有关，单位是像素。
     *
     * @param imageBuf 图片文件的路径
     * @param posX     打印起点X坐标
     * @param posY     打印起点Y坐标
     * @param direction      图片的方向. 请参照PRN_DIRECTION的定义
     * @param align    图片的对齐基准点. 请参照PRN_ ALIGNMENT的定义
     */
    public void PRN_AddImageToLabelEx(byte[] imageBuf, int posX, int posY, PRN_DIRECTION direction, PRN_ALIGNMENT align) {
        try {
            int iImgSize = imageBuf.length;
            if (iImgSize <= 0) {
                Log.e(TAG, "--PRN_AddImageToLabelEx()--参数无效--");
                return;
            }

            byte[] byteTemp = ("DIR " + direction.value() + ":AN " + align.value() + ":PP " + posX + "," + posY + ":PRBUF " + iImgSize + "\n").getBytes();
            byteTemp = this.ByteMerger(byteTemp, imageBuf);
            byteTemp = this.ByteMerger(byteTemp, "\n".getBytes());
            this.m_byteToPrint = this.ByteMerger(this.m_byteToPrint, byteTemp);
        } catch (Exception var8) {
            var8.printStackTrace();
        }
    }

    /**
     * 追加指令
     */
    public void PRN_AddCommand(byte[] data) {
        if (data[data.length - 1] != 13 && data[data.length - 1] != 10) {
            data = this.ByteMerger(data, "\n".getBytes());
        }

        this.m_byteToPrint = this.ByteMerger(this.m_byteToPrint, data);
    }

    /**
     * 设置打印份数
     *
     * @param numOfCopies 数量
     */
    public void PRN_PrintLabel(int numOfCopies) {
        this.m_byteToPrint = this.ByteMerger(this.m_byteToPrint, ("PF " + numOfCopies + "\nVERBOFF\n").getBytes());
    }

    /**
     * 设定打印机
     *
     * @param id    需要设定的配置项，使用 DLL/OCX 请参考 HoneywellPrint.h 中PRN_CFG_ID 的定义，及各配置项的可用值的定义。使用 JavaClass 请参考
     *              HoneywellPrinter.class 中各配置项名的定义及其可用值的定义
     * @param value 设定值
     */
    public void PRN_SetCfg(PRN_CFG_ID id, int value) {
        switch (id) {
            case PRN_CFG_MEDIA_TYPE:
                if (value >= 0 && value < PRN_MEDIA_TYPE.values().length) {
                    this.m_printConfig.iChangeFlg |= this.BIT(CONFIG_BIT.CONFIG_BIT_MEDIA_TYPE.value());
                    this.m_printConfig.tMediaType = value;
                } else {
                    Log.e(TAG, "--PRN_SetCfg()--参数无效");
                }
                break;
            case PRN_CFG_PRINT_METHOD:
                if (value >= 0 && value < PRN_PRINT_METHOD.values().length) {
                    this.m_printConfig.iChangeFlg |= this.BIT(CONFIG_BIT.CONFIG_BIT_PRINT_METHOD.value());
                    this.m_printConfig.tPrintMethod = value;
                } else {
                    Log.e(TAG, "--PRN_SetCfg()--参数无效");
                }
                break;
            case PRN_CFG_MEDIA_MARGINX:
                this.m_printConfig.iChangeFlg |= this.BIT(CONFIG_BIT.CONFIG_BIT_MEDIA_MARGINX.value());
                this.m_printConfig.iMediaMarginX = value;
                break;
            case PRN_CFG_MEDIA_WIDTH:
                if (value <= 0) {
                    Log.e(TAG, "--PRN_SetCfg()--参数无效");
                } else {
                    this.m_printConfig.iChangeFlg |= this.BIT(CONFIG_BIT.CONFIG_BIT_MEDIA_WIDTH.value());
                    this.m_printConfig.iMediaWidth = value;
                }
                break;
            case PRN_CFG_MEDIA_LENGTH:
                if (value <= 0) {
                    Log.e(TAG, "--PRN_SetCfg()--参数无效");
                } else {
                    this.m_printConfig.iChangeFlg |= this.BIT(CONFIG_BIT.CONFIG_BIT_MEDIA_LENGTH.value());
                    this.m_printConfig.iMediaLength = value;
                }
                break;
            case PRN_CFG_TOP_ADJUST:
                this.m_printConfig.iChangeFlg |= this.BIT(CONFIG_BIT.CONFIG_BIT_TOP_ADJUST.value());
                this.m_printConfig.iTopAdjust = value;
                break;
            case PRN_CFG_REST_ADJUST:
                this.m_printConfig.iChangeFlg |= this.BIT(CONFIG_BIT.CONFIG_BIT_REST_ADJUST.value());
                this.m_printConfig.iRestAdjust = value;
                break;
            case PRN_CFG_MEDIA_CALIBRATE_MODE:
                if (value >= 0 && value < PRN_CALIBRATE_MODE.values().length) {
                    this.m_printConfig.iChangeFlg |= this.BIT(CONFIG_BIT.CONFIG_BIT_MEIDA_CALIBRATE_MODE.value());
                    this.m_printConfig.tCalibrateMode = value;
                } else {
                    Log.e(TAG, "--PRN_SetCfg()--参数无效");
                }
                break;
            case PRN_CFG_POWER_UP_ACTION:
                if (value >= 0 && value < PRN_ACTION_TYPE.values().length) {
                    this.m_printConfig.iChangeFlg |= this.BIT(CONFIG_BIT.CONFIG_BIT_POWER_UP_ACTION.value());
                    this.m_printConfig.tPowerUpAction = value;
                } else {
                    Log.e(TAG, "--PRN_SetCfg()--参数无效");
                }
                break;
            case PRN_CFG_HEAD_DOWN_ACTION:
                if (value >= 0 && value < PRN_ACTION_TYPE.values().length) {
                    this.m_printConfig.iChangeFlg |= this.BIT(CONFIG_BIT.CONFIG_BIT_HEAD_DOWN_ACTION.value());
                    this.m_printConfig.tHeadDownAction = value;
                } else {
                    Log.e(TAG, "--PRN_SetCfg()--参数无效");
                }
                break;
            case PRN_CFG_DARKNESS:
                if (value > 0 && value <= 100) {
                    this.m_printConfig.iChangeFlg |= this.BIT(CONFIG_BIT.CONFIG_BIT_DARKNESS.value());
                    this.m_printConfig.iDarkness = value;
                } else {
                    Log.e(TAG, "--PRN_SetCfg()--参数无效");
                }
                break;
            case PRN_CFG_PRINT_SPEED:
                if (value < 0) {
                    Log.e(TAG, "--PRN_SetCfg()--参数无效");
                } else {
                    this.m_printConfig.iChangeFlg |= this.BIT(CONFIG_BIT.CONFIG_BIT_PRINT_SPEED.value());
                    this.m_printConfig.tPrintSpeed = value;
                }
                break;
            default:
                Log.e(TAG, "--PRN_SetCfg()--参数无效");
        }
    }

    /**
     * 执行需更改(已调用 PRN_SetCfg 的所有打印设定
     */
    public void PRN_WriteConfig() {
        if (this.m_printConfig.iChangeFlg == 0) {
            Log.e(TAG, "--PRN_WriteConfig()--配置是空的");
            return;
        }

        String pszCmdBuffer = "";
        String szTmpBuffer = "";
        if (this.ISCHANGED(this.m_printConfig.iChangeFlg, CONFIG_BIT.CONFIG_BIT_MEDIA_TYPE.value())) {
            pszCmdBuffer = pszCmdBuffer + g_szPrnCmd_Enter;
            szTmpBuffer = String.format(g_szSetupMediaType, g_szMediaTypes[this.m_printConfig.tMediaType]);
            pszCmdBuffer = pszCmdBuffer + szTmpBuffer;
        }

        if (this.ISCHANGED(this.m_printConfig.iChangeFlg, CONFIG_BIT.CONFIG_BIT_PRINT_METHOD.value())) {
            pszCmdBuffer = pszCmdBuffer + g_szPrnCmd_Enter;
            szTmpBuffer = String.format(g_szSetupPrintMethod, g_szPrintMethods[this.m_printConfig.tPrintMethod]);
            pszCmdBuffer = pszCmdBuffer + szTmpBuffer;
        }

        if (this.ISCHANGED(this.m_printConfig.iChangeFlg, CONFIG_BIT.CONFIG_BIT_MEDIA_MARGINX.value())) {
            pszCmdBuffer = pszCmdBuffer + g_szPrnCmd_Enter;
            szTmpBuffer = String.format(g_szSetupMediaMarginX, this.m_printConfig.iMediaMarginX);
            pszCmdBuffer = pszCmdBuffer + szTmpBuffer;
        }

        if (this.ISCHANGED(this.m_printConfig.iChangeFlg, CONFIG_BIT.CONFIG_BIT_MEDIA_WIDTH.value())) {
            pszCmdBuffer = pszCmdBuffer + g_szPrnCmd_Enter;
            szTmpBuffer = String.format(g_szSetupMediaWidth, this.m_printConfig.iMediaWidth);
            pszCmdBuffer = pszCmdBuffer + szTmpBuffer;
        }

        if (this.ISCHANGED(this.m_printConfig.iChangeFlg, CONFIG_BIT.CONFIG_BIT_MEDIA_LENGTH.value())) {
            pszCmdBuffer = pszCmdBuffer + g_szPrnCmd_Enter;
            szTmpBuffer = String.format(g_szSetupMediaLength, this.m_printConfig.iMediaLength);
            pszCmdBuffer = pszCmdBuffer + szTmpBuffer;
        }

        if (this.ISCHANGED(this.m_printConfig.iChangeFlg, CONFIG_BIT.CONFIG_BIT_TOP_ADJUST.value())) {
            pszCmdBuffer = pszCmdBuffer + g_szPrnCmd_Enter;
            szTmpBuffer = String.format(g_szSetupTopAdjust, this.m_printConfig.iTopAdjust);
            pszCmdBuffer = pszCmdBuffer + szTmpBuffer;
        }

        if (this.ISCHANGED(this.m_printConfig.iChangeFlg, CONFIG_BIT.CONFIG_BIT_REST_ADJUST.value())) {
            pszCmdBuffer = pszCmdBuffer + g_szPrnCmd_Enter;
            szTmpBuffer = String.format(g_szSetupRestAdjust, this.m_printConfig.iRestAdjust);
            pszCmdBuffer = pszCmdBuffer + szTmpBuffer;
        }

        if (this.ISCHANGED(this.m_printConfig.iChangeFlg, CONFIG_BIT.CONFIG_BIT_MEIDA_CALIBRATE_MODE.value())) {
            pszCmdBuffer = pszCmdBuffer + g_szPrnCmd_Enter;
            szTmpBuffer = String.format(g_szSetupMediaCalibrateMode, g_szCalibrateModes[this.m_printConfig.tCalibrateMode]);
            pszCmdBuffer = pszCmdBuffer + szTmpBuffer;
        }

        if (this.ISCHANGED(this.m_printConfig.iChangeFlg, CONFIG_BIT.CONFIG_BIT_POWER_UP_ACTION.value())) {
            pszCmdBuffer = pszCmdBuffer + g_szPrnCmd_Enter;
            szTmpBuffer = String.format(g_szSetupPowerUpAction, g_szPowerUpActions[this.m_printConfig.tPowerUpAction]);
            pszCmdBuffer = pszCmdBuffer + szTmpBuffer;
        }

        if (this.ISCHANGED(this.m_printConfig.iChangeFlg, CONFIG_BIT.CONFIG_BIT_HEAD_DOWN_ACTION.value())) {
            pszCmdBuffer = pszCmdBuffer + g_szPrnCmd_Enter;
            szTmpBuffer = String.format(g_szSetupHeadDownAction, g_szHeadDownActions[this.m_printConfig.tHeadDownAction]);
            pszCmdBuffer = pszCmdBuffer + szTmpBuffer;
        }

        if (this.ISCHANGED(this.m_printConfig.iChangeFlg, CONFIG_BIT.CONFIG_BIT_PRINT_SPEED.value())) {
            pszCmdBuffer = pszCmdBuffer + g_szPrnCmd_Enter;
            if (this.m_printConfig.tPrintSpeed >= PRN_PRINT_SPEED.values().length) {
                szTmpBuffer = String.format(g_szSetupPrintSpeed, this.m_printConfig.tPrintSpeed);
            } else {
                szTmpBuffer = String.format(g_szSetupPrintSpeed, PRN_PRINT_SPEED.values()[this.m_printConfig.tPrintSpeed].value());
            }

            pszCmdBuffer = pszCmdBuffer + szTmpBuffer;
        }

        if (this.ISCHANGED(this.m_printConfig.iChangeFlg, CONFIG_BIT.CONFIG_BIT_DARKNESS.value())) {
            pszCmdBuffer = pszCmdBuffer + g_szPrnCmd_Enter;
            szTmpBuffer = String.format(g_szSetupDarkness, this.m_printConfig.iDarkness);
            pszCmdBuffer = pszCmdBuffer + szTmpBuffer;
        }

        m_byteToPrint = this.ByteMerger(m_byteToPrint, pszCmdBuffer.getBytes());
    }

    /**
     * 合并两个byte
     */
    private byte[] ByteMerger(byte[] byte_1, byte[] byte_2) {
        if (null == byte_1) {
            return byte_2;
        } else if (null == byte_2) {
            return byte_1;
        } else {
            byte[] byte_3 = new byte[byte_1.length + byte_2.length];
            System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
            System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
            return byte_3;
        }
    }

    /**
     * 验证是否属于中文字符
     */
    private boolean IsChinese(String strIn) {
        for (int i = 0; i < strIn.length(); ++i) {
            String strTemp = strIn.substring(i, i + 1);
            if (Pattern.matches("[一-龥]", strTemp)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 将文件输入流转换为byte
     */
    private byte[] GetFileByte(InputStream in) throws IOException {
        try {
            int fileSize = in.available();
            if (fileSize > 2147483647) {
                System.out.println("file too big...");
                return null;
            } else {
                byte[] buffer = new byte[fileSize];
                int offset = 0;

                int numRead;
                for (boolean var5 = false; offset < buffer.length && (numRead = in.read(buffer, offset, buffer.length - offset)) >= 0; offset += numRead) {
                }

                if (offset != buffer.length) {
                    throw new IOException("Could not completely read file ");
                } else {
                    in.close();
                    return buffer;
                }
            }
        } catch (Exception var6) {
            throw var6;
        }
    }

    private int BIT(int iIn) {
        return 1 << iIn;
    }

    private boolean ISCHANGED(int flg, int n) {
        return (flg & this.BIT(n)) != 0;
    }

    /**
     * 将文件输入流转换为byte
     */
    private byte[] getFileByte(InputStream in) throws IOException {
        try {
            int fileSize = in.available();
            if (fileSize > 2147483647) {
                System.out.println("file too big...");
                return null;
            } else {
                byte[] buffer = new byte[fileSize];
                int offset = 0;

                int numRead;
                for (boolean var5 = false; offset < buffer.length && (numRead = in.read(buffer, offset, buffer.length - offset)) >= 0; offset += numRead) {
                }

                if (offset != buffer.length) {
                    throw new IOException("Could not completely read file ");
                } else {
                    in.close();
                    return buffer;
                }
            }
        } catch (Exception var6) {
            throw var6;
        }
    }

    /**
     * 霍尼韦尔打印机设置信息
     */
    static class _HoneywellPrintConfig implements Serializable {
        int iChangeFlg;
        int tMediaType;
        int tPrintMethod;
        int tCalibrateMode;
        int tPrintSpeed;
        int tPowerUpAction;
        int tHeadDownAction;
        int iMediaMarginX;
        int iMediaWidth;
        int iMediaLength;
        int iTopAdjust;
        int iRestAdjust;
        int iDarkness;

        _HoneywellPrintConfig() {
        }
    }

    //-------------------------------------------以下为枚举定义---------------------------------------

    /**
     * 对齐方向
     */
    public static enum PRN_DIRECTION {
        PRN_DIRECTION_1(1),
        PRN_DIRECTION_2(2),
        PRN_DIRECTION_3(3),
        PRN_DIRECTION_4(4);

        private int _direct;

        private PRN_DIRECTION(int i) {
            this._direct = i;
        }

        public static PRN_DIRECTION valueOf(int value) {
            switch (value) {
                case 1:
                    return PRN_DIRECTION_1;
                case 2:
                    return PRN_DIRECTION_2;
                case 3:
                    return PRN_DIRECTION_3;
                case 4:
                    return PRN_DIRECTION_4;
                default:
                    return PRN_DIRECTION_1;
            }
        }

        public int value() {
            return this._direct;
        }
    }


    /**
     * 对齐方式
     */
    public static enum PRN_ALIGNMENT {
        PRN_ANCHOR_1(1),
        PRN_ANCHOR_2(2),
        PRN_ANCHOR_3(3),
        PRN_ANCHOR_4(4),
        PRN_ANCHOR_5(5),
        PRN_ANCHOR_6(6),
        PRN_ANCHOR_7(7),
        PRN_ANCHOR_8(8),
        PRN_ANCHOR_9(9);

        private int _value;

        private PRN_ALIGNMENT(int i) {
            this._value = i;
        }

        public static PRN_ALIGNMENT valueOf(int value) {
            switch (value) {
                case 1:
                    return PRN_ANCHOR_1;
                case 2:
                    return PRN_ANCHOR_2;
                case 3:
                    return PRN_ANCHOR_3;
                case 4:
                    return PRN_ANCHOR_4;
                case 5:
                    return PRN_ANCHOR_5;
                case 6:
                    return PRN_ANCHOR_6;
                case 7:
                    return PRN_ANCHOR_7;
                case 8:
                    return PRN_ANCHOR_8;
                case 9:
                    return PRN_ANCHOR_9;
                default:
                    return PRN_ANCHOR_1;
            }
        }

        public int value() {
            return this._value;
        }
    }

    /**
     * 设置信息id
     */
    public static enum PRN_CFG_ID {
        PRN_CFG_MEDIA_TYPE,
        PRN_CFG_PRINT_METHOD,
        PRN_CFG_MEDIA_MARGINX,
        PRN_CFG_MEDIA_WIDTH,
        PRN_CFG_MEDIA_LENGTH,
        PRN_CFG_TOP_ADJUST,
        PRN_CFG_REST_ADJUST,
        PRN_CFG_MEDIA_CALIBRATE_MODE,
        PRN_CFG_POWER_UP_ACTION,
        PRN_CFG_HEAD_DOWN_ACTION,
        PRN_CFG_DARKNESS,
        PRN_CFG_PRINT_SPEED;

        private PRN_CFG_ID() {
        }
    }

    public static enum PRN_MEDIA_TYPE {
        PRN_MEDIA_TYPE_BLACK_MARK(0),
        PRN_MEDIA_TYPE_GAP(1),
        PRN_MEDIA_TYPE_CONTINUOUS_FIX(2),
        PRN_MEDIA_TYPE_CONTINUOUS_VAR(3);

        private int m_value;

        private PRN_MEDIA_TYPE(int value) {
            this.m_value = value;
        }

        public int value() {
            return this.m_value;
        }
    }

    public static enum CONFIG_BIT {
        CONFIG_BIT_MEDIA_TYPE(5),
        CONFIG_BIT_PRINT_METHOD(6),
        CONFIG_BIT_MEDIA_MARGINX(7),
        CONFIG_BIT_MEDIA_WIDTH(8),
        CONFIG_BIT_MEDIA_LENGTH(9),
        CONFIG_BIT_TOP_ADJUST(10),
        CONFIG_BIT_REST_ADJUST(11),
        CONFIG_BIT_MEIDA_CALIBRATE_MODE(12),
        CONFIG_BIT_POWER_UP_ACTION(13),
        CONFIG_BIT_HEAD_DOWN_ACTION(14),
        CONFIG_BIT_PRINT_SPEED(15),
        CONFIG_BIT_DARKNESS(16);

        private int index;

        private CONFIG_BIT(int idx) {
            this.index = idx;
        }

        public int value() {
            return this.index;
        }
    }

    public static enum PRN_PRINT_METHOD {
        PRN_PRINT_METHOD_NO_RIBBON,
        PRN_PRINT_METHOD_RIBBON;

        private PRN_PRINT_METHOD() {
        }
    }

    public static enum PRN_CALIBRATE_MODE {
        PRN_CALIBRATE_MODE_SLOW,
        PRN_CALIBRATE_MODE_FAST;

        private PRN_CALIBRATE_MODE() {
        }
    }

    public static enum PRN_ACTION_TYPE {
        PRN_ACTION_TYPE_NO,
        PRN_ACTION_TYPE_FORM_FEED,
        PRN_ACTION_TYPE_TEST_FEED,
        PRN_ACTION_TYPE_SMART_CALIBRATE;

        private PRN_ACTION_TYPE() {
        }
    }

    public static enum PRN_PRINT_SPEED {
        PRN_PRINT_SPEED_50(50),
        PRN_PRINT_SPEED_75(75),
        PRN_PRINT_SPEED_100(100),
        PRN_PRINT_SPEED_150(150),
        PRN_PRINT_SPEED_200(200);

        private int m_value;

        private PRN_PRINT_SPEED(int idx) {
            this.m_value = idx;
        }

        public int value() {
            return this.m_value;
        }
    }
}
