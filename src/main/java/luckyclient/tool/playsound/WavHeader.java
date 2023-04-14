package luckyclient.tool.playsound;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

class WavHeader {
    /**
     * 4 资源交换文件标志（RIFF�?
     */
    public final char fileID[] = {'R', 'I', 'F', 'F'};
    /**
     * 4 总字节数
     */
    public int fileLength;
    /**
     * 4 WAV文件标志（WAVE�?
     */
    public char wavTag[] = {'W', 'A', 'V', 'E'};
    /**
     * 4 波形格式标志（fmt ），�?后一位空�?
     */
    public char fmtHdrID[] = {'f', 'm', 't', ' '};
    /**
     * 4 过滤字节（一般为00000010H），若为00000012H则说明数据头携带附加信息
     */
    public int fmtHdrLeth;
    /**
     * 2 格式种类（�?�为1时，表示数据为线性PCM编码�?
     */
    public short formatTag;
    /**
     * 2 通道数，单声道为1，双声道�?2
     */
    public short channels;
    /**
     * 4 采样频率
     */
    public int samplesPerSec;
    /**
     * 4 波形数据传输速率（每秒平均字节数�?
     */
    public int avgBytesPerSec;
    /**
     * 2 DATA数据块长度，字节
     */
    public short blockAlign;
    /**
     * 2 PCM位宽
     */
    public short bitsPerSample;
    /**
     * 4 数据标志符（data�?
     */
    public char dataHdrID[] = {'d', 'a', 't', 'a'};
    /**
     * 4 DATA总数据长度字�?
     */
    public int dataHdrLeth;
    public byte[] getHeader() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        WriteChar(bos, fileID);
        WriteInt(bos, fileLength);
        WriteChar(bos, wavTag);
        WriteChar(bos, fmtHdrID);
        WriteInt(bos, fmtHdrLeth);
        WriteShort(bos, formatTag);
        WriteShort(bos, channels);
        WriteInt(bos, samplesPerSec);
        WriteInt(bos, avgBytesPerSec);
        WriteShort(bos, blockAlign);
        WriteShort(bos, bitsPerSample);
        WriteChar(bos, dataHdrID);
        WriteInt(bos, dataHdrLeth);
        bos.flush();
        byte[] r = bos.toByteArray();
        bos.close();
        return r;
    }
    private void WriteShort(ByteArrayOutputStream bos, int s) throws IOException {
        byte[] mybyte = new byte[2];
        mybyte[1] = (byte) ((s << 16) >> 24);
        mybyte[0] = (byte) ((s << 24) >> 24);
        bos.write(mybyte);
    }
    private void WriteInt(ByteArrayOutputStream bos, int n) throws IOException {
        byte[] buf = new byte[4];
        buf[3] = (byte) (n >> 24);
        buf[2] = (byte) ((n << 8) >> 24);
        buf[1] = (byte) ((n << 16) >> 24);
        buf[0] = (byte) ((n << 24) >> 24);
        bos.write(buf);
    }
    private void WriteChar(ByteArrayOutputStream bos, char[] id) {
        for (int i = 0; i < id.length; i++) {
            char c = id[i];
            bos.write(c);
        }
    }
}