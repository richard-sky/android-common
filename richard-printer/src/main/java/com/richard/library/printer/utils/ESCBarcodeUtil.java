package com.richard.library.printer.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 通用ESC条码打印工具类（兼容大部分ESC/POS打印机）
 * 核心优化：全条码子集支持、自动校验位计算、复杂内容兼容性
 */
public final class ESCBarcodeUtil {

    // region 常量定义
    /** 条码类型集合 */
    public enum BarcodeType {
        UPC_A, UPC_E, EAN13, EAN8, CODE39, ITF, CODABAR, CODE93, CODE128
    }

    /** 文本位置 */
    public enum TextPosition {
        NONE(0), ABOVE(1), BELOW(2), BOTH(3); // 使用整型值兼容更多打印机[1](@ref)
        final int code;
        TextPosition(int code) { this.code = code; }
    }

    /** 字符集支持 */
    public enum BarcodeCharset {
        DEFAULT(0, "ASCII"),
        UTF8(1, "UTF-8"),      // 支持中文UTF-8
        GB18030(2, "GB18030"); // 中文打印机专用[5](@ref)

        final int code;
        final String charsetName;
        BarcodeCharset(int code, String charsetName) {
            this.code = code;
            this.charsetName = charsetName;
        }
    }
    // endregion

    // region 核心打印方法
    /**
     * 生成通用条码打印指令
     * @param type      条码类型
     * @param content   条码内容
     * @param height    条码高度(1-255)
     * @param width     条码宽度(2-6)
     * @param textPos   文本位置
     * @param charset   字符集
     * @return 兼容性ESC指令字节
     */
    public static byte[] generateBarcode(BarcodeType type,
                                         String content,
                                         int height,
                                         int width,
                                         TextPosition textPos,
                                         BarcodeCharset charset) {
        // 参数校验
        validateParams(type, content, height, width);

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            output.write(0x0A); // 换行

            // 1. 设置字符集（支持中文打印）[5](@ref)
            if (charset != BarcodeCharset.DEFAULT) {
                output.write(new byte[]{0x1B, 0x74, (byte) charset.code});
            }

            // 2. 设置条码高度（GS h）[4](@ref)
            output.write(new byte[]{0x1D, 0x68, (byte) constrain(height, 1, 255)});

            // 3. 设置条码宽度（GS w）[1](@ref)
            output.write(new byte[]{0x1D, 0x77, (byte) constrain(width, 2, 6)});

            // 4. 设置文本位置（GS H）
            output.write(new byte[]{0x1D, 0x48, (byte) textPos.code});

            // 5. 预处理条码内容（关键优化：校验和/起始终止符）
            String processedContent = preprocessContent(type, content);
            byte[] barcodeData = processedContent.getBytes(charset.charsetName);

            // 6. 发送条码指令（兼容模式）[1,4](@ref)
            output.write(createBarcodeCommand(type, barcodeData.length));
            output.write(barcodeData);

            return output.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("指令生成失败: " + e.getMessage(), e);
        }
    }
    // endregion

    // region 私有方法
    /** 参数校验（增强） */
    private static void validateParams(BarcodeType type, String content, int height, int width) {
        if (height < 1 || height > 255) throw new IllegalArgumentException("高度值超出范围(1-255)");
        if (width < 2 || width > 6) throw new IllegalArgumentException("宽度值超出范围(2-6)");

        switch (type) {
            case UPC_A:
                if (!content.matches("\\d{11,12}")) throw new IllegalArgumentException("UPC-A需11/12位数字");
                break;
            case UPC_E:
                if (!content.matches("\\d{6,8}")) throw new IllegalArgumentException("UPC-E需6-8位数字");
                break;
            case EAN13:
                if (!content.matches("\\d{12,13}")) throw new IllegalArgumentException("EAN-13需12/13位数字");
                break;
            case EAN8:
                if (!content.matches("\\d{7,8}")) throw new IllegalArgumentException("EAN-8需7/8位数字");
                break;
            case CODE39:
                if (!content.matches("[A-Z0-9 \\-.$/+%]*")) throw new IllegalArgumentException("CODE39包含非法字符");
                break;
            case ITF: // ITF需偶数位数字[7](@ref)
                if (!content.matches("\\d+") || content.length() % 2 != 0)
                    throw new IllegalArgumentException("ITF需偶数位数字");
                break;
            case CODE128:
                if (content.isEmpty()) throw new IllegalArgumentException("CODE128内容不能为空");
                break; // 移除ASCII限制（支持UTF-8/GB18030）
        }
    }

    /** 生成条码指令头 */
    private static byte[] createBarcodeCommand(BarcodeType type, int dataLength) {
        byte typeCode = getTypeCode(type);
        return new byte[] { 0x1D, 0x6B, typeCode, (byte) dataLength };
    }

    /** 获取条码类型编码 */
    private static byte getTypeCode(BarcodeType type) {
        switch (type) {
            case UPC_A:   return 0x41;
            case UPC_E:   return 0x42;
            case EAN13:   return 0x43;
            case EAN8:    return 0x44;
            case CODE39:  return 0x45;
            case ITF:     return 0x46;
            case CODABAR: return 0x47;
            case CODE93:  return 0x48;
            case CODE128: return 0x49;
            default:      throw new IllegalArgumentException("未知条码类型");
        }
    }

    /** 预处理内容（关键优化） */
    private static String preprocessContent(BarcodeType type, String raw) {
        switch (type) {
            case UPC_A:
                return raw.length() == 11 ? raw + calculateMod10Checksum(raw) : raw;
            case UPC_E:
                // UPC-E特殊处理：转换为UPC-A格式计算校验位[9](@ref)
                if (raw.length() == 6) {
                    String upcA = expandUPCEtoUPCA("0" + raw);
                    return "0" + raw + calculateMod10Checksum(upcA.substring(0, 11));
                }
                return raw;
            case EAN13:
                return raw.length() == 12 ? raw + calculateMod10Checksum(raw) : raw;
            case EAN8:
                return raw.length() == 7 ? raw + calculateMod10Checksum(raw) : raw;
            case ITF:
                // ITF需确保偶数长度（校验位+原始内容）[7](@ref)
                String withChecksum = raw + calculateMod10Checksum(raw);
                return (withChecksum.length() % 2 == 0) ? withChecksum : "0" + withChecksum;
            case CODE39:
                return "*" + raw + "*";  // 添加起始终止符
            case CODE128:
                // 智能选择子集：纯数字用高密度模式，混合内容用标准模式[7](@ref)
                if (raw.matches("\\d+") && raw.length() % 2 == 0) {
                    return "{C" + raw;  // 高密度数字模式
                } else {
                    return "{B" + raw;  // 标准ASCII模式
                }
            default:
                return raw;
        }
    }

    /** 通用Mod10校验位计算（EAN/UPC/ITF共用）[9](@ref) */
    private static String calculateMod10Checksum(String data) {
        int sum = 0;
        boolean isEven = false; // 从右向左计算
        for (int i = data.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(data.charAt(i));
            sum += digit * (isEven ? 1 : 3);
            isEven = !isEven;
        }
        return String.valueOf((10 - sum % 10) % 10);
    }

    /** 将UPC-E转换为UPC-A格式（用于校验位计算） */
    private static String expandUPCEtoUPCA(String upce) {
        if (upce.length() != 7) throw new IllegalArgumentException("UPC-E必须7位（含系统位）");

        char lastChar = upce.charAt(6);
        String numberPart = upce.substring(1, 6);

        switch (lastChar) {
            case '0': case '1': case '2':
                return upce.charAt(0) + numberPart.substring(0, 2)
                        + lastChar + "0000" + numberPart.substring(2);
            case '3':
                return upce.charAt(0) + numberPart.substring(0, 3)
                        + "00000" + numberPart.substring(3);
            case '4':
                return upce.charAt(0) + numberPart.substring(0, 4)
                        + "0000" + lastChar;
            default: // 5-9
                return upce.charAt(0) + numberPart.substring(0, 5)
                        + "0000" + lastChar;
        }
    }

    /** 数值范围限制 */
    private static int constrain(int value, int min, int max) {
        return Math.min(max, Math.max(min, value));
    }
    // endregion

    // region 简化调用方法
    /** 默认参数快速生成 */
    public static byte[] generateSimpleBarcode(BarcodeType type, String content) {
        return generateBarcode(type, content, 50, 3, TextPosition.BELOW, BarcodeCharset.DEFAULT);
    }

    /** 支持中文的条码生成 */
    public static byte[] generateChineseBarcode(BarcodeType type, String content) {
        return generateBarcode(type, content, 50, 3, TextPosition.BELOW, BarcodeCharset.GB18030);
    }
    // endregion
}