package lejos.hardware.video;

public class VideoUtils
{
    public static int fourcc(char a, char b, char c, char d)
    {
        return ((int)a & 0xff) | (((int)b & 0xff) << 8) | (((int)c & 0xff) << 16) | (((int)d & 0xff) << 24);
    }
}
