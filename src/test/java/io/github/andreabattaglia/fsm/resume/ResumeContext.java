package io.github.andreabattaglia.fsm.resume;

import io.github.andreabattaglia.fsm.FsmContext;

public class ResumeContext implements FsmContext
{
    private boolean transientResolved = false;

    public boolean isTransientResolved()
    {
        return transientResolved;
    }

    public void setTransientResolved(boolean transientResolved)
    {
        this.transientResolved = transientResolved;
    }
}
