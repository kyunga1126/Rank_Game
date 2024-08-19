$(document).ready(function() {
    $('#initialize-round').click(function() {
        var roundName = $('#round-selection').val();
        console.log('Initializing round:', roundName);

        $.post('/worldcup/initialize', { roundName: roundName })
            .done(function(data) {
                console.log('Round initialized successfully:', data);
                alert(roundName + ' 라운드가 성공적으로 선택되었습니다!');
                $('#game-rounds').show();
                $('#game-results').hide();
                $('#next-round').show();
                $('#page-title').text('게임 월드컵');
                $('#section-title').text('게임 월드컵');
                loadRoundGames(roundName);
            })
            .fail(function(jqXHR, textStatus, errorThrown) {
                console.error('Round initialization failed:', textStatus, errorThrown);
                alert(roundName + ' 라운드 선택에 실패했습니다.');
            });
    });

    $('#view-results').click(function() {
        $('#game-rounds').hide();
        $('#game-results').show();
        $('#page-title').text('게임 월드컵 결과');
        $('#section-title').text('게임 월드컵 결과');
        loadResults();
    });

    $('#next-round').click(function() {
        var roundName = $('#round-selection').val();
        var nextRound;

        switch (roundName) {
            case '32강':
                nextRound = '16강';
                break;
            case '16강':
                nextRound = '8강';
                break;
            case '8강':
                nextRound = '4강';
                break;
            case '4강':
                nextRound = '결승';
                break;
            default:
                alert('다음 라운드가 없습니다.');
                return;
        }

        $.get('/worldcup/rounds/' + roundName, function(data) {
            var winners = data.filter(round => round.winner).map(round => round.winner.id);

            if (winners.length < 2) {
                alert('다음 라운드를 시작하기 위해 최소 두 개의 승자가 필요합니다.');
                return;
            }

            $.post('/worldcup/initialize', { roundName: nextRound, winners: winners })
                .done(function() {
                    $('#round-selection').val(nextRound);
                    $('#initialize-round').click();
                })
                .fail(function(jqXHR, textStatus, errorThrown) {
                    console.error('Failed to initialize next round:', textStatus, errorThrown);
                    alert(nextRound + ' 라운드를 시작하는 데 실패했습니다.');
                });
        });
    });

    function loadRoundGames(roundName) {
        console.log('Loading games for round:', roundName);
        $.get('/worldcup/rounds/' + roundName)
            .done(function(data) {
                console.log('Games loaded:', data);
                var roundGamesContainer = $('#round-games');
                roundGamesContainer.empty();
                $.each(data, function(index, round) {
                    roundGamesContainer.append(
                        '<div class="col-md-6">' +
                        '<div class="game-item">' +
                        '<div class="game-matchup">' +
                        '<div class="game">' +
                        '<img src="' + round.game1.imageUrl + '" alt="' + round.game1.gameName + '" />' +
                        '<h5>' + round.game1.gameName + '</h5>' +
                        '<button class="vote-btn" data-round-id="' + round.id + '" data-game-id="' + round.game1.id + '">' + round.game1.gameName + '</button>' +
                        '</div>' +
                        '<div class="vs">VS</div>' +
                        '<div class="game">' +
                        '<img src="' + round.game2.imageUrl + '" alt="' + round.game2.gameName + '" />' +
                        '<h5>' + round.game2.gameName + '</h5>' +
                        '<button class="vote-btn" data-round-id="' + round.id + '" data-game-id="' + round.game2.id + '">' + round.game2.gameName + '</button>' +
                        '</div>' +
                        '</div>' +
                        '</div>' +
                        '</div>'
                    );
                });
                bindVoteButtons(roundName);
            })
            .fail(function(jqXHR, textStatus, errorThrown) {
                console.error('Failed to load games:', textStatus, errorThrown);
                alert('게임 로드를 실패했습니다.');
            });
    }

    function loadResults() {
        console.log('Loading results');
        $.get('/worldcup/results/win-rates')
            .done(function(data) {
                console.log('Results loaded:', data);
                var resultsContainer = $('#round-results');
                resultsContainer.empty();

                if (data.length === 0) {
                    resultsContainer.append('<p>결과가 없습니다.</p>');
                    return;
                }

                data.sort(function(a, b) {
                    return b.winCount - a.winCount || b.winRate - a.winRate;
                });

                $.each(data, function(index, result) {
                    resultsContainer.append(
                        '<div class="game-card">' +
                        '<img src="' + result.imageUrl + '" alt="' + result.gameName + '"/>' +
                        '<h5>' + result.gameName + '</h5>' +
                        '<div class="win-rate">' + result.winRate.toFixed(2) + '% 우승 비율</div>' +
                        '<div class="win-count">우승 횟수: ' + result.winCount + '</div>' +
                        '</div>'
                    );
                });
            })
            .fail(function(jqXHR, textStatus, errorThrown) {
                console.error('Failed to load results:', textStatus, errorThrown);
                alert('결과를 로드하는 데 실패했습니다.');
            });
    }

    function bindVoteButtons(roundName) {
        $('.vote-btn').click(function() {
            var roundId = $(this).data('round-id');
            var gameId = $(this).data('game-id');
            console.log('Voting for game:', gameId, 'in round:', roundId);

            $.post('/worldcup/rounds/' + roundId + '/vote', { winnerId: gameId })
                .done(function() {
                    console.log('Vote successful');
                    alert('투표가 완료되었습니다.');

                    if (roundName === '결승') {
                        $('.vote-btn').prop('disabled', true);
                        $('[data-game-id="' + gameId + '"]').closest('.game').addClass('winner');
                    }
                })
                .fail(function(jqXHR, textStatus, errorThrown) {
                    console.error('Vote failed:', textStatus, errorThrown);
                    alert('투표에 실패했습니다.');
                });
        });
    }

    loadResults();
});
