/**
 * Gráficos do dashboard administrativo.
 * Os dados chegam em window.DASHBOARD, preenchido pelo template.
 * As cores são lidas das variáveis CSS, para os gráficos seguirem a paleta do sistema
 * sem duplicar os valores aqui.
 */
document.addEventListener('DOMContentLoaded', function () {
    const dados = window.DASHBOARD;
    if (!dados || typeof Chart === 'undefined') return;

    const estilo = getComputedStyle(document.documentElement);
    const cor = nome => estilo.getPropertyValue(nome).trim();

    const paleta = {
        petroleo: cor('--yph-petrol'),
        musgo: cor('--yph-moss'),
        pedra: cor('--yph-stone'),
        borda: cor('--yph-borda')
    };

    Chart.defaults.font.family = cor('--bs-body-font-family');
    Chart.defaults.color = paleta.pedra;
    Chart.defaults.plugins.legend.display = false;
    Chart.defaults.maintainAspectRatio = false;

    const eixos = {
        x: { grid: { display: false } },
        y: { beginAtZero: true, ticks: { precision: 0 }, grid: { color: paleta.borda } }
    };

    desenhar('graficoMeses', {
        type: 'line',
        data: {
            labels: dados.meses.rotulos,
            datasets: [{
                data: dados.meses.valores,
                borderColor: paleta.petroleo,
                backgroundColor: comTransparencia(paleta.petroleo, 0.12),
                fill: true,
                tension: 0.3,
                pointRadius: 4,
                pointBackgroundColor: paleta.petroleo
            }]
        },
        options: { scales: eixos }
    });

    // Mesma ordem do enum StatusConsulta: agendadas, realizadas, canceladas.
    // A legenda em HTML usa a mesma ordem pelas classes .cor-0, .cor-1, .cor-2.
    desenhar('graficoStatus', {
        type: 'doughnut',
        data: {
            labels: dados.status.rotulos,
            datasets: [{
                data: dados.status.valores,
                backgroundColor: [paleta.petroleo, paleta.musgo, paleta.pedra],
                borderWidth: 0
            }]
        },
        options: { cutout: '68%' }
    });

    desenhar('graficoTipos', {
        type: 'bar',
        data: {
            labels: dados.tipos.rotulos,
            datasets: [{
                data: dados.tipos.valores,
                backgroundColor: paleta.petroleo,
                borderRadius: 4,
                maxBarThickness: 28
            }]
        },
        options: {
            indexAxis: 'y',
            scales: {
                x: { beginAtZero: true, ticks: { precision: 0 }, grid: { color: paleta.borda } },
                y: { grid: { display: false } }
            }
        }
    });
});

function desenhar(id, configuracao) {
    const canvas = document.getElementById(id);
    if (canvas) new Chart(canvas, configuracao);
}

function comTransparencia(hex, alfa) {
    const valor = parseInt(hex.replace('#', ''), 16);
    const r = (valor >> 16) & 255;
    const g = (valor >> 8) & 255;
    const b = valor & 255;
    return `rgba(${r}, ${g}, ${b}, ${alfa})`;
}