package com.jitterted.ebp.blackjack.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class GameOutcomeTest {

    @Test
    public void playerNotDealtBlackjackThenIsNotDoneAfterInitialDeal() throws Exception {
        Deck playerNotDealtBlackjack = new StubDeck(Rank.NINE, Rank.EIGHT,
                                                    Rank.TEN, Rank.QUEEN);
        Game game = new Game(playerNotDealtBlackjack);

        game.initialDeal();

        assertThat(game.isPlayerDone())
                .isFalse();
    }

    @Test
    public void playerDealtBlackjackUponInitialDealWinsBlackjackAndIsDone() throws Exception {
        Deck playerDealtBlackjack = new StubDeck(Rank.ACE, Rank.SEVEN,
                                                 Rank.JACK, Rank.TEN);
        Game game = new Game(playerDealtBlackjack);

        game.initialDeal();

        assertThat(game.determineOutcome())
                .isEqualTo(GameOutcome.PLAYER_WINS_BLACKJACK);
        assertThat(game.isPlayerDone())
                .isTrue();
    }

    @Test
    public void playerHitsAndGoesBustThenPlayerLoses() throws Exception {
        Game game = new Game(StubDeck.playerHitsAndGoesBust());
        game.initialDeal();

        game.playerHits();
        game.dealerTurn();

        assertThat(game.determineOutcome())
                .isEqualTo(GameOutcome.PLAYER_BUSTED);
    }

    @Test
    public void playerDealtBetterHandThanDealerAndStandsThenPlayerBeatsDealer() throws Exception {
        Game game = new Game(StubDeck.playerStandsAndBeatsDealer());
        game.initialDeal();

        game.playerStands();
        game.dealerTurn();

        assertThat(game.determineOutcome())
                .isEqualTo(GameOutcome.PLAYER_BEATS_DEALER);
    }

}