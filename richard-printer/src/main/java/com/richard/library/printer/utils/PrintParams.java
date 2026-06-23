package com.richard.library.printer.utils;

import static com.richard.library.printer.enumerate.EllipsizeMode.COLUMN_LINE;

import android.graphics.Bitmap;
import android.text.TextUtils;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.richard.library.printer.command.PrinterCmdUtil;
import com.richard.library.printer.enumerate.Align;
import com.richard.library.printer.enumerate.EllipsizeMode;
import com.richard.library.printer.enumerate.TicketSpec;
import com.richard.library.printer.model.ColumnItem;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * author Richard
 * date 2020/8/18 16:28
 * version V1.0
 * description: 打印机打印参数构造
 * 注：目前只适配了58和80规格的小票
 */
public class PrintParams extends ArrayList<byte[]> {

    private static final long serialVersionUID = 5569224436177287937L;

    /**
     * 空白占位符byte
     */
    public static final byte SPACE_BYTE = 32;

    /**
     * 分割线字符
     */
    private char splitCharacter = 45;

    /**
     * byte 编码格式
     */
    private Charset charset = Charset.forName("GBK");

    /**
     * 小票规格，默认80
     */
    private final TicketSpec spec;

    /**
     * 打印份数
     */
    private int printNum = 1;

    /**
     * 小票打印文本内容(不含指令内容、图片内容)
     */
    private final StringBuilder content = new StringBuilder();

    /**
     * 回调
     */
    private Callback callback;


    public PrintParams(@NonNull TicketSpec spec) {
        this.spec = spec;
    }

    /**
     * 获取小票规格
     */
    public TicketSpec getSpec() {
        return spec;
    }

    /**
     * 获取当前字符集编码
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * 设置打印份数
     */
    public void setPrintNum(int printNum) {
        this.printNum = printNum;
    }

    /**
     * 获取打印份数
     */
    public int getPrintNum() {
        return printNum;
    }

    @Override
    public void clear() {
        super.clear();
        this.content.setLength(0);
    }

    /**
     * 设置byte编码
     */
    public void setCharset(@NonNull Charset charset) {
        this.charset = charset;
    }

    /**
     * 获取当前分隔线字符
     */
    public char getSplitCharacter() {
        return splitCharacter;
    }

    /**
     * 设置分割线字符
     */
    public void setSplitCharacter(char splitCharacter) {
        this.splitCharacter = splitCharacter;
    }

    /**
     * 设置回调
     */
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    //----------------------------------------------------------------------------------------------

    /**
     * 添加元素
     */
    public boolean add(byte item) {
        return this.add(new byte[]{item});
    }

    /**
     * 添加元素
     */
    public boolean add(byte[] item) {
        if (item == null || item.length <= 0) {
            return false;
        }
        this.invokeCallback(item);
        return super.add(item);
    }

    /**
     * 添加图片bitmap
     *
     * @param bitmap 图片bitmap
     * @param width  宽px
     * @param height 高px
     */
    public boolean add(Bitmap bitmap, int width, int height, Align align) {
        if (bitmap == null) {
            return false;
        }

        Bitmap compressBitmap = PrintImageUtil.compressPic(
                bitmap, width, height, getBitmapPaddingLeft(getSpec(), width, align));
        byte[] bytes = PrintImageUtil.draw2PxPoint(compressBitmap);
        boolean result = super.add(bytes);
        this.invokeCallback(getSpec(), compressBitmap, bytes, compressBitmap.getWidth(), compressBitmap.getHeight());
        return result;
    }

    /**
     * 添加元素
     */
    public boolean add(String item) {
        if (TextUtils.isEmpty(item)) {
            return false;
        }
        this.add(item, this.getLineMaxLength(0), 0, false, null, false);
        return true;
    }

    /**
     * 添加元素
     *
     * @param item  item元素
     * @param align 文字对齐方式
     */
    public void add(String item, Align align) {
        this.add(item, this.getLineMaxLength(0), 0, false, align, false);
    }

    /**
     * 添加元素
     *
     * @param item     item元素
     * @param fontSize 字体倍数值（仅支持0-1）
     */
    public void add(String item, @IntRange(from = 0, to = 1) int fontSize) {
        this.add(item, this.getLineMaxLength(fontSize), fontSize, false, null, false);
    }

    /**
     * 添加元素
     *
     * @param item     item元素
     * @param fontSize 字体倍数值（仅支持0-1）
     * @param isBold   是否加粗
     */
    public void add(String item, @IntRange(from = 0, to = 1) int fontSize, boolean isBold) {
        this.add(item, this.getLineMaxLength(fontSize), fontSize, isBold, null, false);
    }

    /**
     * 添加元素
     *
     * @param item     item元素
     * @param fontSize 字体倍数值（仅支持0-1）
     * @param isBold   是否加粗
     * @param align    内容对齐方式
     */
    public void add(String item, @IntRange(from = 0, to = 1) int fontSize, boolean isBold, Align align) {
        this.add(item, this.getLineMaxLength(fontSize), fontSize, isBold, align, false);
    }

    /**
     * 添加空白字符
     *
     * @param num 需添加的数量
     */
    public boolean addSpace(int num) {
        if (num <= 0) {
            return false;
        }
        byte[] spaceByteArray = new byte[num];
        Arrays.fill(spaceByteArray, SPACE_BYTE);

        String text = new String(spaceByteArray, charset);
        content.append(text);
        this.invokeCallback(text, 0, false);

        return super.add(spaceByteArray);
    }

    /**
     * 添加换行
     *
     * @param count 换行行数
     */
    public void addNextRow(int count) {
        for (int i = 0; i < count; i++) {
            this.addNextRow();
        }
    }

    /**
     * 添加换行
     *
     * @param count 换行行数
     */
    public void addNextRowSpace(int count) {
        for (int i = 0; i < count; i++) {
            this.addNextRow(" ");
        }
    }

    /**
     * 添加换行
     */
    public void addNextRow() {
        this.addNextRow(null);
    }

    /**
     * 添加换行
     *
     * @param text 选填，换行符后的文本
     */
    private void addNextRow(String text) {
        //byte 10  = "\n"
        if (TextUtils.isEmpty(text)) {
            content.append("\n");
            this.invokeCallback("\n", 0, false);
            super.add(new byte[]{10});
        } else {
            //该方式可解决某些打印机对仅仅只有换行符时没效果的问题
            String finalText = "\n" + text;
            content.append(finalText);
            this.invokeCallback(finalText, 0, false);
            super.add(getByte(finalText, charset));
        }
    }

    /**
     * 添加分隔线
     *
     * @param fontSize    分割线倍数值
     * @param isAloneLine 是否单独一行显示
     */
    public void addSplitLine(@IntRange(from = 0, to = 1) int fontSize, boolean isAloneLine) {
        if (isAloneLine) {
            this.addNextRow();
        }
        char[] line = new char[this.getLineMaxLength(fontSize)];
        Arrays.fill(line, splitCharacter);
        this.add(String.valueOf(line), getLineMaxLength(fontSize), fontSize, false, null, isAloneLine);
    }

    /**
     * 添加元素
     *
     * @param item              item元素
     * @param allocColumnLength 分配的列最大字节长度
     * @param fontSize          字体倍数值（仅支持0-1）
     * @param isBold            是否加粗
     * @param align             内容对齐方式
     * @param isFillRight       是否以空白字符填充文本右边空间
     */
    private void add(String item, int allocColumnLength, @IntRange(from = 0, to = 1) int fontSize,
                     boolean isBold, Align align, boolean isFillRight) {
        if (item == null) {
            return;
        }

        //设置字体大小
        this.add(PrinterCmdUtil.fontSizeSetBig(fontSize));

        //字体加粗
        if (isBold) {
            this.add(PrinterCmdUtil.emphasizedOn());
        } else {
            this.add(PrinterCmdUtil.emphasizedOff());
        }

        byte[] itemBytes = PrintParams.getByte(item, charset);

        //对齐方式
        if (align == null) {
            align = Align.LEFT;
        }

        int spaceCount;
        switch (align) {
            case CENTER:
                spaceCount = (allocColumnLength - itemBytes.length) / 2;
                break;
            case RIGHT:
                spaceCount = allocColumnLength - itemBytes.length;
                break;
            case LEFT:
            default:
                spaceCount = 0;
        }

        //用空白字符填充item文本左边剩余空间
        this.addSpace(spaceCount);

        //添加item项内容
        this.add(itemBytes);
        content.append(item);
        this.invokeCallback(item, fontSize, isBold);

        //用空白字符填充item文本右边剩余空间
        switch (align) {
            case LEFT:
                if (!isFillRight) {
                    return;
                }
                this.addSpace(allocColumnLength - itemBytes.length);
                break;
            case CENTER:
                this.addSpace(spaceCount);
                break;
        }
    }

    /**
     * 添加一行
     *
     * @param text  文本
     * @param align 对齐方式
     */
    public void addRow(String text, int fontSize, boolean isBold, Align align) {
        this.addNextRow();
        this.add(text, fontSize, isBold, align);
    }

    /**
     * 添加一行
     *
     * @param columns 列文本
     */
    public void addRow(String... columns) {
        float[] widthWeigh = new float[columns.length];
        Arrays.fill(widthWeigh, 1);
        this.addRow(
                0
                , false
                , widthWeigh
                , Align.LEFT
                , EllipsizeMode.LINE
                , columns
        );
    }

    /**
     * 添加一行
     *
     * @param ellipsizeMode 列文本显示模式
     * @param columns       列文本
     */
    public void addRow(EllipsizeMode ellipsizeMode, String... columns) {
        float[] widthWeigh = new float[columns.length];
        Arrays.fill(widthWeigh, 1);
        this.addRow(
                0
                , false
                , widthWeigh
                , Align.LEFT
                , ellipsizeMode
                , columns
        );
    }

    /**
     * 添加一行
     *
     * @param columns 列文本
     */
    public void addRow(@IntRange(from = 0, to = 1) int fontSize, String... columns) {
        float[] widthWeigh = new float[columns.length];
        Arrays.fill(widthWeigh, 1);
        this.addRow(
                fontSize
                , false
                , widthWeigh
                , Align.LEFT
                , EllipsizeMode.LINE
                , columns
        );
    }

    /**
     * 添加一行
     *
     * @param fontSize   字体倍数值（仅支持0-1）
     * @param widthWeigh 列占宽权重，widthWeigh数量和columns数量必须一致
     * @param columns    列文本
     */
    public void addRow(@IntRange(from = 0, to = 1) int fontSize, @NonNull float[] widthWeigh, String... columns) {
        this.addRow(
                fontSize
                , false
                , widthWeigh
                , Align.LEFT
                , EllipsizeMode.LINE
                , columns
        );
    }

    /**
     * 添加一行
     *
     * @param fontSize 字体倍数值（仅支持0-1）
     * @param isBold   是否加粗
     * @param columns  列文本
     */
    public void addRow(@IntRange(from = 0, to = 1) int fontSize, boolean isBold, String... columns) {
        float[] widthWeigh = new float[columns.length];
        Arrays.fill(widthWeigh, 1);
        this.addRow(
                fontSize
                , isBold
                , widthWeigh
                , Align.LEFT
                , EllipsizeMode.LINE
                , columns
        );
    }

    /**
     * 添加一行
     *
     * @param fontSize      字体倍数值（仅支持0-1）
     * @param isBold        是否加粗
     * @param ellipsizeMode 列文本显示模式
     * @param columns       列文本
     */
    public void addRow(@IntRange(from = 0, to = 1) int fontSize, boolean isBold, EllipsizeMode ellipsizeMode, String... columns) {
        float[] widthWeigh = new float[columns.length];
        Arrays.fill(widthWeigh, 1);
        this.addRow(
                fontSize
                , isBold
                , widthWeigh
                , Align.LEFT
                , ellipsizeMode
                , columns
        );
    }

    /**
     * 添加一行
     *
     * @param fontSize      字体倍数值（仅支持0-1）
     * @param isBold        是否加粗
     * @param widthWeigh    列占宽权重，widthWeigh数量和columns数量必须一致
     * @param align         对齐方式
     * @param ellipsizeMode 列文本显示模式
     * @param columns       列文本，widthWeigh数量和columns数量必须一致
     */
    public void addRow(@IntRange(from = 0, to = 1) int fontSize, boolean isBold, @NonNull float[] widthWeigh, Align align, EllipsizeMode ellipsizeMode, @NonNull String... columns) {
        if (widthWeigh.length != columns.length) {
            throw new IllegalArgumentException("widthWeigh 或者 columns的元素数量必须一致");
        }

        if (ellipsizeMode == null) {
            ellipsizeMode = EllipsizeMode.LINE;
        }

        int lineMaxLength = this.getLineMaxLength(fontSize);
        int totalAllocatedColumnLength = 0;//总共已分配的列长度

        float totalColumnWeigh = 0;
        for (float item : widthWeigh) {
            totalColumnWeigh += item;
        }

        //换行
        this.addNextRow();

        int allocColumnLength;//该列分配总长度
        int columnTextLength;//列文本内容实际长度

        //分解打印内容
        int totalSize = 0;//总数据数量
        List<List<String>> fullSplitList = new ArrayList<>();
        List<String> subList = null;
        for (int index = 0; index < columns.length; index++) {
            String columnItem = columns[index];
            if (ellipsizeMode == COLUMN_LINE) {
                if (index == columns.length - 1) {
                    //最后一列
                    allocColumnLength = lineMaxLength - totalAllocatedColumnLength;
                } else {
                    allocColumnLength = (int) Math.floor(widthWeigh[index] / totalColumnWeigh * lineMaxLength);
                }
                totalAllocatedColumnLength += allocColumnLength;
                subList = StringUtil.substring(columnItem, charset, allocColumnLength);
            } else {
                subList = new ArrayList<>();
                subList.add(columnItem);
            }

            totalSize += subList.size();
            fullSplitList.add(subList);
        }

        //添加打印内容
        int rowIndex = 0;
        while (totalSize > 0) {
            totalAllocatedColumnLength = 0;
            for (int index = 0; index < fullSplitList.size(); index++) {
                List<String> item = fullSplitList.get(index);

                //最后一列
                if (index == fullSplitList.size() - 1) {
                    allocColumnLength = lineMaxLength - totalAllocatedColumnLength;
                } else {
                    allocColumnLength = (int) Math.floor(widthWeigh[index]
                            / totalColumnWeigh * lineMaxLength);
                }

                //--无内容打印的列以空字符填充
                if (rowIndex >= item.size()) {
                    this.addSpace(allocColumnLength);
                    totalAllocatedColumnLength += allocColumnLength;
                    continue;
                }

                //--若该列还有内容未打印完
                totalSize--;
                String columnItem = item.get(rowIndex);
                columnTextLength = getBytesLength(columnItem, charset);

                //添加列文本内容
                if (ellipsizeMode == COLUMN_LINE || columnTextLength <= allocColumnLength) {
                    this.addColumn(columnItem, columnTextLength, allocColumnLength, lineMaxLength,
                            fontSize, isBold, align);
                    totalAllocatedColumnLength += allocColumnLength;
                    continue;
                }

                switch (ellipsizeMode) {
                    case LINE:
                        //添加列文本内容
                        int firstLimit = lineMaxLength - totalAllocatedColumnLength;
                        this.addColumn(columnItem, columnTextLength, allocColumnLength,
                                lineMaxLength, fontSize, isBold, align, firstLimit);

                        if (fullSplitList.size() > 1 && index < fullSplitList.size() - 1) {
                            this.addNextRow();
                            //占满左边空白列
                            int leftPadLength = index + 1;
                            for (int lineIndex = 0; lineIndex < leftPadLength; lineIndex++) {
                                int leftPlaceholderLength = (int) Math.floor(widthWeigh[lineIndex]
                                        / totalColumnWeigh * lineMaxLength);
                                this.addSpace(leftPlaceholderLength);
                            }
                        }
                        break;
                    case NEXT_LINE:
                        //添加列文本内容
                        if (totalAllocatedColumnLength > 0) {
                            this.addNextRow();
                        }

                        this.addColumn(columnItem, columnTextLength, allocColumnLength,
                                lineMaxLength, fontSize, isBold, align);

                        if (columnTextLength > lineMaxLength && fullSplitList.size() > 1 && index < fullSplitList.size() - 1) {
                            this.addNextRow();
                        }
                        break;
                    case ELLIPSIS:
                    case SINGLE_LINE:
                        String columnText;
                        if(ellipsizeMode == EllipsizeMode.ELLIPSIS){
                            columnText = columnItem.substring(0,
                                    (int) Math.floor(allocColumnLength / 2D) - 2).concat("...");
                        }else{
                            columnText = columnItem.substring(0, (int) Math.floor(allocColumnLength / 2D));
                        }

                        columnTextLength = getBytesLength(columnText, charset);
                        this.addColumn(columnText, columnTextLength, allocColumnLength, lineMaxLength,
                                fontSize, isBold, align);
                        break;
                }

                totalAllocatedColumnLength += allocColumnLength;
            }

            if (fullSplitList.size() > 1 && totalSize > 0) {
                this.addNextRow();
            }
            rowIndex++;
        }
    }

    /**
     * 添加一行
     *
     * @param columns 列文本
     */
    public void addRow(ColumnItem... columns) {
        float[] widthWeigh = new float[columns.length];
        Arrays.fill(widthWeigh, 1);
        this.addRow(0, widthWeigh, columns);
    }

    /**
     * 添加一行
     *
     * @param fontSize 字体倍数值（仅支持0-1）
     * @param columns  列文本
     */
    public void addRow(@IntRange(from = 0, to = 1) int fontSize, ColumnItem... columns) {
        float[] widthWeigh = new float[columns.length];
        Arrays.fill(widthWeigh, 1);
        this.addRow(fontSize, widthWeigh, columns);
    }

    /**
     * 添加一行
     *
     * @param fontSize 字体倍数值（仅支持0-1）
     * @param columns  列文本
     */
    public void addRow(@IntRange(from = 0, to = 1) int fontSize, @NonNull float[] widthWeigh, ColumnItem... columns) {
        if (widthWeigh.length != columns.length) {
            throw new IllegalArgumentException("widthWeigh 或者 columns的元素数量必须一致");
        }

        int lineMaxLength = this.getLineMaxLength(fontSize);
        int totalAllocatedColumnLength = 0;//总共已分配的列长度

        float totalColumnWeigh = 0;
        for (float item : widthWeigh) {
            totalColumnWeigh += item;
        }

        //换行
        this.addNextRow();

        int allocColumnLength;//该列分配总长度
        int columnTextLength;//列文本内容实际长度

        //分解打印内容
        int totalSize = 0;//总数据数量
        List<String> subList = null;
        for (int index = 0; index < columns.length; index++) {
            ColumnItem columnItem = columns[index];
            if (columnItem.getEllipsizeMode() == COLUMN_LINE) {
                if (index == columns.length - 1) {
                    //最后一列
                    allocColumnLength = lineMaxLength - totalAllocatedColumnLength;
                } else {
                    allocColumnLength = (int) Math.floor(widthWeigh[index] / totalColumnWeigh * lineMaxLength);
                }
                totalAllocatedColumnLength += allocColumnLength;
                subList = StringUtil.substring(columnItem.getText(), charset, allocColumnLength);
            } else {
                subList = new ArrayList<>();
                subList.add(columnItem.getText());
            }

            totalSize += subList.size();
            columnItem.setSubstringList(subList);
        }

        //添加打印内容
        int rowIndex = 0;
        while (totalSize > 0) {
            totalAllocatedColumnLength = 0;
            for (int index = 0; index < columns.length; index++) {
                ColumnItem item = columns[index];

                //最后一列
                if (index == columns.length - 1) {
                    allocColumnLength = lineMaxLength - totalAllocatedColumnLength;
                } else {
                    allocColumnLength = (int) Math.floor(widthWeigh[index]
                            / totalColumnWeigh * lineMaxLength);
                }

                //--无内容打印的列以空字符填充
                if (rowIndex >= item.getSubstringList().size()) {
                    this.addSpace(allocColumnLength);
                    totalAllocatedColumnLength += allocColumnLength;
                    continue;
                }

                //--若该列还有内容未打印完
                totalSize--;
                String columnItem = item.getSubstringList().get(rowIndex);
                columnTextLength = getBytesLength(columnItem, charset);

                //添加列文本内容
                if (item.getEllipsizeMode() == COLUMN_LINE || columnTextLength <= allocColumnLength) {
                    this.addColumn(columnItem, columnTextLength, allocColumnLength, lineMaxLength,
                            fontSize, item.isBold(), item.getAlign());
                    totalAllocatedColumnLength += allocColumnLength;
                    continue;
                }

                switch (item.getEllipsizeMode()) {
                    case LINE:
                        //添加列文本内容
                        int firstLimit = lineMaxLength - totalAllocatedColumnLength;
                        this.addColumn(columnItem, columnTextLength, allocColumnLength,
                                lineMaxLength, fontSize, item.isBold(), item.getAlign(), firstLimit);

                        if (columns.length > 1 && index < columns.length - 1) {
                            this.addNextRow();
                            //占满左边空白列
                            int leftPadLength = index + 1;
                            for (int lineIndex = 0; lineIndex < leftPadLength; lineIndex++) {
                                int leftPlaceholderLength = (int) Math.floor(widthWeigh[lineIndex]
                                        / totalColumnWeigh * lineMaxLength);
                                this.addSpace(leftPlaceholderLength);
                            }
                        }
                        break;
                    case NEXT_LINE:
                        //添加列文本内容
                        if (totalAllocatedColumnLength > 0) {
                            this.addNextRow();
                        }

                        this.addColumn(columnItem, columnTextLength, allocColumnLength,
                                lineMaxLength, fontSize, item.isBold(), item.getAlign());

                        if (columnTextLength > lineMaxLength && columns.length > 1 && index < columns.length - 1) {
                            this.addNextRow();
                        }
                        break;
                    case ELLIPSIS:
                    case SINGLE_LINE:
                        String columnText;
                        if(item.getEllipsizeMode() == EllipsizeMode.ELLIPSIS){
                            columnText = columnItem.substring(0,
                                    (int) Math.floor(allocColumnLength / 2D) - 2).concat("...");
                        }else{
                            columnText = columnItem.substring(0, (int) Math.floor(allocColumnLength / 2D));
                        }

                        columnTextLength = getBytesLength(columnText, charset);
                        this.addColumn(columnText, columnTextLength, allocColumnLength, lineMaxLength,
                                fontSize, item.isBold(), item.getAlign());
                        break;
                }

                totalAllocatedColumnLength += allocColumnLength;
            }

            if (columns.length > 1 && totalSize > 0) {
                this.addNextRow();
            }
            rowIndex++;
        }
    }

    //----------------------------------------------------------------------------------------------

    /**
     * 添加列（兼容了长文本多行显示）
     *
     * @param text              文本
     * @param columnTextLength  文本实际占用字节长度
     * @param allocColumnLength 分配的列最大字节长度
     * @param lineMaxLength     一行最大显示字节长度
     * @param fontSize          字体放大倍数
     * @param isBold            是否加粗
     * @param align             对齐方式
     */
    private void addColumn(String text, int columnTextLength, int allocColumnLength, int lineMaxLength, int fontSize, boolean isBold, Align align) {
        addColumn(text, columnTextLength, allocColumnLength, lineMaxLength, fontSize, isBold, align, 0);
    }

    /**
     * 添加列（兼容了长文本多行显示）
     *
     * @param text              文本
     * @param columnTextLength  文本实际占用字节长度
     * @param allocColumnLength 分配的列最大字节长度
     * @param lineMaxLength     一行最大显示字节长度
     * @param fontSize          字体放大倍数
     * @param isBold            是否加粗
     * @param align             对齐方式
     */
    private void addColumn(String text, int columnTextLength, int allocColumnLength, int lineMaxLength, int fontSize, boolean isBold, Align align, int firstLimit) {
        //添加列文本内容
        if (columnTextLength > allocColumnLength) {
            List<String> splitTextList = StringUtil.substring(text, charset, lineMaxLength, firstLimit);
            for (int i = 0, size = splitTextList.size(); i < size; i++) {
                this.add(splitTextList.get(i), allocColumnLength, fontSize, isBold, align, true);
                if (i < size - 1) {
                    this.addNextRow();
                }
            }
        } else {
            this.add(text, allocColumnLength, fontSize, isBold, align, true);
        }
    }

    /**
     * 获取一行最大字符数
     * 58mm票据打印机：一行可以打印16个汉字，32个字符；80mm票据打印机,一行可以打印24个汉字,48个字符；421D标签打印机，一行可打印34个汉字，69个字符。
     *
     * @param fontSize 目前只适配了0-1的字体倍数
     * @return 最大字符数(以半角字符为基础 ， 比如 ： 中文单个字符长度为2 ， 数字字母长度为1)
     */
    public int getLineMaxLength(@IntRange(from = 0, to = 1) int fontSize) {
        int maxLineLength = 0;
        switch (spec) {
            case SPEC_58:
                switch (fontSize) {
                    case 1:
                        maxLineLength = 16;
                        break;
                    case 0:
                    default:
                        maxLineLength = 32;
                }
                break;
            case SPEC_80:
            default:
                switch (fontSize) {
                    case 1:
                        maxLineLength = 24;
                        break;
                    case 0:
                    default:
                        maxLineLength = 48;
                }
                break;
        }

        return maxLineLength;
    }

    /**
     * 转换为byte数组返回
     */
    public byte[] toByteArray() {
        int size = 0;
        for (byte[] item : this) {
            size += item.length;
        }

        byte[] result = new byte[size];
        int i = 0;
        for (byte[] item : this) {
            for (byte b : item) {
                result[i++] = b;
            }
        }

        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return content.toString();
    }

    /**
     * 回调onAddText
     */
    private void invokeCallback(String text, int fontSize, boolean isBold) {
        if (callback != null) {
            callback.onAddText(getSpec(), text, fontSize, isBold);
        }
    }

    /**
     * 回调onAddByte
     */
    private void invokeCallback(byte[] bytes) {
        if (callback != null) {
            callback.onAddByte(getSpec(), bytes);
        }
    }

    /**
     * 回调onAddBitmap
     *
     * @param spec   小票规格
     * @param bitmap 图片
     * @param bytes  可用于直接打印的黑白图片byte（已包含左内边距）
     * @param width  bitmap宽
     * @param height bitmap高
     */
    private void invokeCallback(TicketSpec spec, Bitmap bitmap, byte[] bytes, int width, int height) {
        if (callback != null) {
            callback.onAddBitmap(spec, bitmap, bytes, width, height);
        }
    }


    public interface Callback {

        /**
         * 当添加文本时回调
         *
         * @param spec     小票规格
         * @param text     文本内容
         * @param fontSize 字体倍数大小
         * @param isBold   是否加粗
         */
        void onAddText(TicketSpec spec, String text, int fontSize, boolean isBold);

        /**
         * 当前添加byte数据时回调
         *
         * @param spec  小票规格
         * @param bytes bytes
         */
        void onAddByte(TicketSpec spec, byte[] bytes);

        /**
         * 当前添加bitmap时回调
         *
         * @param spec   小票规格
         * @param bitmap 打印的图片
         * @param bytes  打印的bytes
         * @param width  宽度
         * @param height 高度
         */
        void onAddBitmap(TicketSpec spec, Bitmap bitmap, byte[] bytes, int width, int height);
    }

    //--------------------------------------静态方法-------------------------------------------------

    /**
     * 获取Image打印的左内边距
     *
     * @param spec        小票规格
     * @param bitmapWidth 小票打印指定宽度
     * @param align       对齐方式
     */
    public static int getBitmapPaddingLeft(TicketSpec spec, int bitmapWidth, Align align) {
        final int max80Width = 570;
        final int max58Width = 390;

        //对齐方式
        if (align == null) {
            align = Align.LEFT;
        }

        switch (align) {
            case CENTER:
                switch (spec) {
                    case SPEC_80:
                        return (max80Width - bitmapWidth) / 2;
                    case SPEC_58:
                    default:
                        return (max58Width - bitmapWidth) / 2;
                }
            case RIGHT:
                switch (spec) {
                    case SPEC_80:
                        return max80Width - bitmapWidth;
                    case SPEC_58:
                    default:
                        return max58Width - bitmapWidth;
                }
            case LEFT:
            default:
                return 0;
        }
    }

    /**
     * 获取text的byte数组
     */
    public static byte[] getByte(String text) {
        return getByte(text, Charset.forName("GBK"));
    }

    /**
     * 获取text的byte数组
     */
    public static byte[] getByte(String text, Charset charset) {
        if (text == null) {
            return null;
        }
        return text.getBytes(charset);
    }

    /**
     * 获取数据长度
     *
     * @param text 文本
     */
    public static int getBytesLength(String text) {
        return getBytesLength(text, Charset.forName("GBK"));
    }

    /**
     * 获取数据长度
     *
     * @param text 文本
     */
    public static int getBytesLength(String text, Charset charset) {
        if (text == null) {
            return 0;
        }
        return text.getBytes(charset).length;
    }
}
