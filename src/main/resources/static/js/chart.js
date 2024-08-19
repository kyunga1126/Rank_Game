$(document).ready(function() {
    var gameRankChart;

    // 초기 Top 10 게임 차트 로드
    loadGameRankChart('daily');
// 주기 선택 변경 이벤트 핸들러
    $('#time-period').change(function() {
        var timePeriod = $(this).val();
        loadGameRankChart(timePeriod);
    });

// Top 10 게임 차트 로드 함수
    function loadGameRankChart(timePeriod) {
        $.get(`/api/games/top10?period=${timePeriod}`, function(data) {
            const labels = data.map(game => game.gameName);
            const votes = data.map(game => game.gameVote);

            if (gameRankChart) {
                // 기존 차트 데이터 업데이트
                gameRankChart.data.labels = labels;
                gameRankChart.data.datasets[0].data = votes;
                gameRankChart.update();
            } else {
                // 새로운 차트 생성
                var ctx = document.getElementById('gameRankChart').getContext('2d');
                gameRankChart = new Chart(ctx, {
                    type: 'bar', // 가로형 바 그래프로 설정
                    data: {
                        labels: labels,
                        datasets: [{
                            label: '득표수',
                            data: votes,
                            backgroundColor: 'rgba(75, 192, 192, 0.2)',
                            borderColor: 'rgba(241, 134, 255)',
                            borderWidth: 1
                        }]
                    },
                    options: {
                        indexAxis: 'y', // 가로형 바 그래프로 설정
                        scales: {
                            x: {
                                beginAtZero: true,
                                ticks: {
                                    color: 'white' // x축 글씨 색상 변경
                                }
                            },
                            y: {
                                ticks: {
                                    color: 'white' // y축 글씨 색상 변경
                                }
                            }
                        },
                        plugins: {
                            legend: {
                                labels: {
                                    color: 'white' // 범례 글씨 색상 변경
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    // 게임별 득표 차트
    var ctx4 = document.getElementById('vote-trend-chart').getContext('2d');
    var voteTrendChart = new Chart(ctx4, {
        type: 'line',
        data: {
            labels: [],
            datasets: [{
                label: '득표수',
                data: [],
                backgroundColor: 'rgba(75, 192, 192, 0.2)',
                borderColor: 'rgba(241, 134, 255)',
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true,
                    reverse: false,  // 순위가 높은 숫자가 상단에 표시되도록 반전
                    ticks: {
                        color: 'white' // y축 글씨 색상 변경
                    }
                },
                x: {
                    ticks: {
                        color: 'white' // x축 글씨 색상 변경
                    }
                }
            },
            plugins: {
                legend: {
                    labels: {
                        color: 'white' // 범례 글씨 색상 변경
                    }
                }
            }
        }
    });

    $("#game-select").change(function() {
        const gameId = $(this).val();
        if (gameId) {
            $.get(`/game/${gameId}/vote-trend`, function(data) {
                const labels = data.map(item => item.voteTime);
                const ranks = data.map(item => item.gameVote);

                voteTrendChart.data.labels = labels;
                voteTrendChart.data.datasets[0].data = ranks;
                voteTrendChart.update();
            });
        }
    });

    // 전날 대비 순위 변동 테이블 로드 함수
    $.get('/api/games/daily-rank-changes', function(data) {
        const tableBody = $("#rank-change-table");
        tableBody.empty(); // 기존 내용을 초기화

        data.forEach((game, index) => {
            const rankChange = game.rankChange;
            let changeClass = '';
            let changeSymbol = '-';
            let changeColor = '';

            if (rankChange > 0) {
                changeClass = 'rank-up';
                changeSymbol = '▲' + rankChange;
                changeColor = '#28a745'; // 올라가면 초록색
            } else if (rankChange < 0) {
                changeClass = 'rank-down';
                changeSymbol = '▼' + Math.abs(rankChange);
                changeColor = '#dc3545'; // 내려가면 빨간색
            } else {
                changeClass = 'no-change';
                changeColor = '#ffffff'; // 안바꼈을때 흰색
            }

            tableBody.append(`
                <tr>
                    <td>${index + 1}</td>
                    <td>${game.gameName}</td>
                    <td class="${changeClass}" style="color: ${changeColor};">${changeSymbol}</td>
                </tr>
            `);
        });
    });
});
