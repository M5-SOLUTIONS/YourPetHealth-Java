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

document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('input[type="radio"][name="perfil"]')
        .forEach(radio => radio.addEventListener('change', alternarCamposVeterinario));
    alternarCamposVeterinario();   // mantém o estado ao voltar com erro de validação

    document.querySelectorAll('[data-alternar-senha]')
        .forEach(botao => botao.addEventListener('click', () => alternarVisibilidadeSenha(botao)));
});