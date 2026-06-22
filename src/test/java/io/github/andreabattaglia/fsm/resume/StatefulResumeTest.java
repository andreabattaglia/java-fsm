package io.github.andreabattaglia.fsm.resume;

import io.github.andreabattaglia.fsm.FsmExecutionResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Demonstrates the stateful resume pattern:
 *
 * <pre>
 *   Episode 1: PENDING → IN_PROGRESS → self-loop (transient not resolved) → episode ends
 *   [persist state = IN_PROGRESS]
 *   Episode 2: IN_PROGRESS → DONE
 * </pre>
 *
 * This mirrors the can-file-mover Job 2 pattern where each group document carries
 * its last persisted state and the FSM resumes from it on each job run.
 */
class StatefulResumeTest
{
    @Test
    void episode1_transientNotResolved_selfLoopLeavesStateAsInProgress()
    {
        ResumeContext ctx = new ResumeContext();
        // transient condition not resolved: InProgressCommand will return SKIP → self-loop

        FsmExecutionResult<ResumeState> episode1 = new ResumeOrchestrator().execute(ctx, ResumeState.PENDING);

        // FSM moved PENDING → IN_PROGRESS (1 step), then self-looped: episode ended
        assertEquals(ResumeState.IN_PROGRESS, episode1.getFinalState());
        assertEquals(1, episode1.getSteps());
    }

    @Test
    void episode2_resumesFromPersistedState_completesSuccessfully()
    {
        ResumeContext ctx = new ResumeContext();

        // Episode 1: run until self-loop
        FsmExecutionResult<ResumeState> episode1 = new ResumeOrchestrator().execute(ctx, ResumeState.PENDING);
        ResumeState persistedState = episode1.getFinalState(); // IN_PROGRESS — would be saved to DB

        // Transient condition resolved between episodes
        ctx.setTransientResolved(true);

        // Episode 2: resume from persisted state
        FsmExecutionResult<ResumeState> episode2 = new ResumeOrchestrator().execute(ctx, persistedState);

        assertEquals(ResumeState.DONE, episode2.getFinalState());
        assertEquals(1, episode2.getSteps()); // only IN_PROGRESS ran
    }

    @Test
    void fullRun_withoutInterruption_completesInOneShot()
    {
        ResumeContext ctx = new ResumeContext();
        ctx.setTransientResolved(true); // no transient: full run without episodes

        FsmExecutionResult<ResumeState> result = new ResumeOrchestrator().execute(ctx, ResumeState.PENDING);

        assertEquals(ResumeState.DONE, result.getFinalState());
        assertEquals(2, result.getSteps()); // PENDING + IN_PROGRESS
    }
}
