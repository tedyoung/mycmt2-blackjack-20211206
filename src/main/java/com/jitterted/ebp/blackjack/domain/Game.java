package com.jitterted.ebp.blackjack.domain;

// An Entity (even though it doesn't have an ID)
// Also an Aggregate Root
public class Game {

    private final Deck deck;

    private final Hand dealerHand = new Hand();
    private final Hand playerHand = new Hand();

    private boolean playerDone;

    public Game() {
        deck = new Deck();
    }

    public void initialDeal() {
        dealRoundOfCards();
        dealRoundOfCards();
    }

    private void dealRoundOfCards() {
        // why: players first because this is the rule
        playerHand.drawFrom(deck);
        dealerHand.drawFrom(deck);
    }

    public String determineOutcome() {
        if (playerHand.isBusted()) {
            return "You Busted, so you lose.  💸";
        } else if (dealerHand.isBusted()) {
            return "Dealer went BUST, Player wins! Yay for you!! 💵";
        } else if (playerHand.beats(dealerHand)) {
            return "You beat the Dealer! 💵";
        } else if (playerHand.pushes(dealerHand)) {
            return "Push: Nobody wins, we'll call it even.";
        } else {
            return "You lost to the Dealer. 💸";
        }
    }

    public void dealerTurn() {
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

    public void playerHits() {
        playerHand.drawFrom(deck);
        playerDone = playerHand.isBusted();
    }

    public void playerStands() {
        playerDone = true;
    }

    public boolean isPlayerDone() {
        return playerDone;
    }
}
