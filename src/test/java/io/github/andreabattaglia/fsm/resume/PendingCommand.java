package io.github.andreabattaglia.fsm.resume;

import io.github.andreabattaglia.fsm.FsmCommand;
import io.github.andreabattaglia.fsm.FsmCommandException;
import io.github.andreabattaglia.fsm.FsmCommandOutcome;

public class PendingCommand implements FsmCommand<ResumeState, ResumeContext>
{
    @Override
    public ResumeState forStateCode()
    {
        return ResumeState.PENDING;
    }

    @Override
    public FsmCommandOutcome execute(ResumeContext ctx) throws FsmCommandException
    {
        return FsmCommandOutcome.SUCCESS;
    }
}
