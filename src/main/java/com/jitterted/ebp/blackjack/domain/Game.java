package com.jitterted.ebp.blackjack.domain;

import com.jitterted.ebp.blackjack.domain.port.GameMonitor;

// An Entity (even though it doesn't have an ID)
// Also an Aggregate Root
public class Game {

    // COLLABORATOR
    private final Deck deck;
    private final GameMonitor gameMonitor;

    private final Hand dealerHand = new Hand();
    private final Hand playerHand = new Hand();

    private boolean playerDone;

    public Game(Deck deck) {
        this.deck = deck;
        this.gameMonitor = game -> {};
    }

    public Game(Deck deck, GameMonitor gameMonitor) {
        this.deck = deck;
        this.gameMonitor = gameMonitor;
    }

    private void dealRoundOfCards() {
        // why: players first because this is the rule
        playerHand.drawFrom(deck);
        dealerHand.drawFrom(deck);
    }

    public GameOutcome determineOutcome() {
        // Need constraint: can't call this method if player is NOT DONE
        if (playerHand.isBusted()) {
            return GameOutcome.PLAYER_BUSTED;
        } else if (dealerHand.isBusted()) {
            return GameOutcome.DEALER_BUSTED;
        } else if (playerHand.hasBlackjack()) {
            return GameOutcome.PLAYER_WINS_BLACKJACK;
        } else if (playerHand.beats(dealerHand)) {
            return GameOutcome.PLAYER_BEATS_DEALER;
        } else if (playerHand.pushes(dealerHand)) {
            return GameOutcome.PLAYER_PUSHES;
        } else {
            return GameOutcome.PLAYER_LOSES;
        }
    }

    private void dealerTurn() {
        // Dealer makes its choice automatically based on a simple heuristic (<=16 must hit, =>17 must stand)
        if (!playerHand.isBusted()) {
            while (dealerHand.dealerMustDrawCard()) {
                dealerHand.drawFrom(deck);
            }
        }
    }

    // Query Rule: Snapshot (point-in-time) & Immutable/Unmodifiable
    //          (not affect internal state of Game)
    // 1. clone() - new Hand(hand.cards())
    //      * extra objects (those get GC'd quickly)
    //      * implies that caller has the "real" Hand
    // 2. DTO - Data Transfer Object - does not belong in domain
    //      * not always immutable
    // 3. Value Object - immutable and belong in the domain
    //      * value() could be dynamic
    // 3b. Memento Pattern ** PREFERRED
    //      * Hand.memento() -> HandView Value Object
    // 4. Interface that is implemented by Hand, e.g., ReadableHand
    //      * then just return ReadableHand
    //      * doesn't qualify for 'snapshot'
    //          - still a reference to underlying Hand that can change
    public Hand playerHand() {
        return playerHand;
    }

    public Hand dealerHand() {
        return dealerHand;
    }

    public void initialDeal() {
        dealRoundOfCards();
        dealRoundOfCards();
        updatePlayerDoneTo(playerHand.hasBlackjack());
    }

    public void playerHits() {
        // only allow if the player is NOT done
        playerHand.drawFrom(deck);
        updatePlayerDoneTo(playerHand.isBusted());
    }

    private void updatePlayerDoneTo(boolean playerIsDone) {
        playerDone = playerIsDone;
        if (playerDone) {
            gameMonitor.roundCompleted(this);
        }
    }

    public void playerStands() {
        dealerTurn();
        updatePlayerDoneTo(true);
    }

    public boolean isPlayerDone() {
        return playerDone;
    }
}
