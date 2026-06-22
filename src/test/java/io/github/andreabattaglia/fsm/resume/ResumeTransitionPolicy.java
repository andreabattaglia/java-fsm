package io.github.andreabattaglia.fsm.resume;

import io.github.andreabattaglia.fsm.FsmCommandOutcome;
import io.github.andreabattaglia.fsm.FsmTransitionPolicy;
import io.github.andreabattaglia.fsm.NoOpFsmCommand;

import java.util.EnumSet;
import java.util.Set;

public class ResumeTransitionPolicy extends FsmTransitionPolicy<ResumeState, ResumeContext>
{
    @Override
    public Set<ResumeState> states()
    {
        return EnumSet.allOf(ResumeState.class);
    }

    @Override
    public ResumeState initialState()
    {
        return ResumeState.PENDING;
    }

    @Override
    public boolean isTerminal(ResumeState state)
    {
        return state == ResumeState.DONE || state == ResumeState.ABORTED;
    }

    @Override
    public ResumeState next(ResumeState current, FsmCommandOutcome outcome, ResumeContext ctx)
    {
        if (outcome == FsmCommandOutcome.FATAL_ERROR)
        {
            return ResumeState.ABORTED;
        }
        return switch (current)
        {
            case PENDING     -> ResumeState.IN_PROGRESS;
            case IN_PROGRESS -> outcome == FsmCommandOutcome.SKIP
                                    ? ResumeState.IN_PROGRESS  // self-loop: come back next episode
                                    : ResumeState.DONE;
            default -> throw new IllegalStateException("no transition from " + current);
        };
    }
}
