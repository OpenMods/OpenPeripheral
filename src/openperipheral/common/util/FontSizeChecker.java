package openperipheral.common.util;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.util.ChatAllowedCharacters;

public class FontSizeChecker {

    private int[] charWidth = new int[256];
    
	public FontSizeChecker(String textureFile) {
		readFontTexture(textureFile);
	}

	private void readFontTexture(String par1Str)
    {
        BufferedImage bufferedimage;

        try
        {
            bufferedimage = ImageIO.read(RenderEngine.class.getResourceAsStream(par1Str));
        }
        catch (IOException ioexception)
        {
            throw new RuntimeException(ioexception);
        }

        int i = bufferedimage.getWidth();
        int j = bufferedimage.getHeight();
        int[] aint = new int[i * j];
        bufferedimage.getRGB(0, 0, i, j, aint, 0, i);
        int k = 0;

        while (k < 256)
        {
            int l = k % 16;
            int i1 = k / 16;
            int j1 = 7;

            while (true)
            {
                if (j1 >= 0)
                {
                    int k1 = l * 8 + j1;
                    boolean flag = true;

                    for (int l1 = 0; l1 < 8 && flag; ++l1)
                    {
                        int i2 = (i1 * 8 + l1) * i;
                        int j2 = aint[k1 + i2] & 255;

                        if (j2 > 0)
                        {
                            flag = false;
                        }
                    }

                    if (flag)
                    {
                        --j1;
                        continue;
                    }
                }

                if (k == 32)
                {
                    j1 = 2;
                }

                this.charWidth[k] = j1 + 2;
                ++k;
                break;
            }
        }
    }
	
    public int getCharWidth(char par1)
    {
        if (par1 == 167)
        {
            return -1;
        }
        else if (par1 == 32)
        {
            return 4;
        }
        else
        {
            int i = ChatAllowedCharacters.allowedCharacters.indexOf(par1);
            if (i != -1) {
            	return this.charWidth[i + 32];
            }
        }
        return 8;
    }
	
    public int getStringWidth(String par1Str)
    {
        if (par1Str == null)
        {
            return 0;
        }
        else
        {
            int i = 0;
            boolean flag = false;

            for (int j = 0; j < par1Str.length(); ++j)
            {
                char c0 = par1Str.charAt(j);
                int k = this.getCharWidth(c0);

                if (k < 0 && j < par1Str.length() - 1)
                {
                    ++j;
                    c0 = par1Str.charAt(j);

                    if (c0 != 108 && c0 != 76)
                    {
                        if (c0 == 114 || c0 == 82)
                        {
                            flag = false;
                        }
                    }
                    else
                    {
                        flag = true;
                    }

                    k = 0;
                }

                i += k;

                if (flag)
                {
                    ++i;
                }
            }

            return i;
        }
    }

}
