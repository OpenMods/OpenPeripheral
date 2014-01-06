package mrtjp.projectred.transmission;

import mrtjp.projectred.api.IBundledEmitter;

public interface IBundledCablePart extends IWirePart, IBundledEmitter
{
    public byte[] getBundledSignal();

    public byte[] calculateSignal();

    public void setSignal(byte[] newSignal);
}
