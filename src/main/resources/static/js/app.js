/**
 * Comportamentos de tela do YourPetHealth.
 * Nada aqui fala com a API: só melhora a interação com o HTML que o servidor já entregou.
 */

/**
 * Confirmação de remoção.
 * O nome vem por data-nome porque o Thymeleaf não permite interpolar
 * strings dentro de atributos de evento (proteção contra injeção).
 */
function confirmarRemocao(form) {
    const nome = form.dataset.nome;
    return confirm('Remover ' + nome + '? Esta ação não pode ser desfeita.');
}

/**
 * Confirmação genérica: a mensagem vem pronta em data-confirmar.
 * Usada para cancelar e excluir consultas.
 */
function confirmarAcao(form) {
    return confirm(form.dataset.confirmar);
}

/**
 * Confirmação ao desativar ou reativar uma conta na área administrativa.
 * Nome e ação vêm por data-*, pelo mesmo motivo de confirmarRemocao.
 */
function confirmarAlteracaoDeAcesso(form) {
    const { nome, acao } = form.dataset;
    const mensagem = acao === 'desativar'
        ? 'Desativar a conta de ' + nome + '? Ela não poderá mais entrar no sistema.'
        : 'Reativar a conta de ' + nome + '?';
    return confirm(mensagem);
}

/**
 * Cadastro: mostra CRMV e especialidade só quando o perfil escolhido é veterinário.
 */
function alternarCamposVeterinario() {
    const campos = document.getElementById('camposVeterinario');
    if (!campos) return;

    const escolhido = document.querySelector('input[type="radio"][name="perfil"]:checked');
    campos.hidden = !escolhido || escolhido.value !== 'VETERINARIO';
}

/**
 * Botão "Mostrar/Ocultar" ao lado de campos de senha.
 * O botão aponta para o campo pelo atributo data-alternar-senha.
 */
function alternarVisibilidadeSenha(botao) {
    const campo = document.getElementById(botao.dataset.alternarSenha);
    if (!campo) return;

    const visivel = campo.type === 'password';
    campo.type = visivel ? 'text' : 'password';
    botao.textContent = visivel ? 'Ocultar' : 'Mostrar';
    botao.setAttribute('aria-pressed', String(visivel));
}

/**
 * Marca no menu o link da seção atual.
 * Vence o link de prefixo mais longo: em /app/pets/3/historico, "Meus pets" fica ativo.
 */
function marcarLinkAtivo() {
    const caminho = window.location.pathname;
    let melhor = null;

    document.querySelectorAll('.navbar-yph .nav-link').forEach(link => {
        const destino = new URL(link.href, window.location.origin).pathname;
        const casa = caminho === destino || caminho.startsWith(destino + '/');
        if (casa && (!melhor || destino.length > melhor.destino.length)) {
            melhor = { link, destino };
        }
    });

    if (melhor) {
        melhor.link.classList.add('active');
        melhor.link.setAttribute('aria-current', 'page');
    }
}

/**
 * Filtro por status feito no navegador, sobre a lista que já veio do servidor.
 * Botões: [data-filtro-alvo="#lista"][data-filtro="TODAS|AGENDADA|..."]
 * Itens da lista: [data-status="..."]
 * Mensagem para filtro sem resultado: elemento com id "<alvo>-vazio".
 */
function aplicarFiltro(botao) {
    const lista = document.querySelector(botao.dataset.filtroAlvo);
    if (!lista) return;

    const filtro = botao.dataset.filtro;

    document.querySelectorAll('[data-filtro-alvo="' + botao.dataset.filtroAlvo + '"]')
        .forEach(b => {
            const ativo = b === botao;
            b.classList.toggle('ativo', ativo);
            b.setAttribute('aria-pressed', String(ativo));
        });

    let visiveis = 0;
    lista.querySelectorAll('[data-status]').forEach(item => {
        const mostrar = filtro === 'TODAS' || item.dataset.status === filtro;
        item.hidden = !mostrar;
        if (mostrar) visiveis++;
    });

    const vazio = document.querySelector(botao.dataset.filtroAlvo + '-vazio');
    if (vazio) vazio.hidden = visiveis > 0;
}

/**
 * Contador de caracteres para textareas com data-contador="idDoContador".
 * Se houver data-minimo, o contador fica em destaque enquanto não atingir o mínimo.
 */
function atualizarContador(campo) {
    const contador = document.getElementById(campo.dataset.contador);
    if (!contador) return;

    const total = campo.value.trim().length;
    const minimo = Number(campo.dataset.minimo || 0);
    const maximo = campo.getAttribute('maxlength');

    contador.textContent = maximo ? total + ' / ' + maximo : String(total);
    contador.classList.toggle('abaixo-minimo', minimo > 0 && total < minimo);
}

/**
 * Evita duplo envio: desabilita o botão após o primeiro clique válido.
 * Só atua em formulários marcados com data-enviar-uma-vez.
 */
function travarEnvio(form) {
    const botao = form.querySelector('button[type="submit"]');
    if (!botao) return;

    botao.disabled = true;
    if (botao.dataset.textoEnviando) {
        botao.textContent = botao.dataset.textoEnviando;
    }
}

document.addEventListener('DOMContentLoaded', function () {
    marcarLinkAtivo();

    // Cadastro
    document.querySelectorAll('input[type="radio"][name="perfil"]')
        .forEach(radio => radio.addEventListener('change', alternarCamposVeterinario));
    alternarCamposVeterinario();   // mantém o estado ao voltar com erro de validação

    document.querySelectorAll('[data-alternar-senha]')
        .forEach(botao => botao.addEventListener('click', () => alternarVisibilidadeSenha(botao)));

    // Confirmações
    document.querySelectorAll('form[data-nome][data-remover]')
        .forEach(form => form.addEventListener('submit', evento => {
            if (!confirmarRemocao(form)) evento.preventDefault();
        }));

    document.querySelectorAll('form[data-confirmar]')
        .forEach(form => form.addEventListener('submit', evento => {
            if (!confirmarAcao(form)) evento.preventDefault();
        }));

    // Filtros
    document.querySelectorAll('[data-filtro-alvo]')
        .forEach(botao => botao.addEventListener('click', () => aplicarFiltro(botao)));

    // Contadores de caracteres
    document.querySelectorAll('textarea[data-contador]').forEach(campo => {
        campo.addEventListener('input', () => atualizarContador(campo));
        atualizarContador(campo);
    });

    // Campos que enviam o formulário ao mudar (data da agenda)
    document.querySelectorAll('[data-enviar-ao-mudar]')
        .forEach(campo => campo.addEventListener('change', () => {
            if (campo.value) campo.form.submit();
        }));

    // Duplo envio
    document.querySelectorAll('form[data-enviar-uma-vez]')
        .forEach(form => form.addEventListener('submit', evento => {
            if (!evento.defaultPrevented) travarEnvio(form);
        }));
});

// Ao voltar pelo botão "voltar" do navegador, a página pode vir do cache com o botão travado
window.addEventListener('pageshow', function () {
    document.querySelectorAll('form[data-enviar-uma-vez] button[type="submit"]')
        .forEach(botao => { botao.disabled = false; });
});