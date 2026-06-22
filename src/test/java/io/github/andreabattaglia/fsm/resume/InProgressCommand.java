package io.github.andreabattaglia.fsm.resume;

import io.github.andreabattaglia.fsm.FsmCommand;
import io.github.andreabattaglia.fsm.FsmCommandException;
import io.github.andreabattaglia.fsm.FsmCommandOutcome;

public class InProgressCommand implements FsmCommand<ResumeState, ResumeContext>
{
    @Override
    public ResumeState forStateCode()
    {
        return ResumeState.IN_PROGRESS;
    }

    @Override
    public FsmCommandOutcome execute(ResumeContext ctx) throws FsmCommandException
    {
        // Transient condition not yet resolved: nothing to do in this episode.
        // The transition policy maps SKIP → IN_PROGRESS (self-loop),
        // which terminates the episodic run and leaves the state persisted as IN_PROGRESS.
        if (!ctx.isTransientResolved())
        {
            return FsmCommandOutcome.SKIP;
        }
        return FsmCommandOutcome.SUCCESS;
    }
}
