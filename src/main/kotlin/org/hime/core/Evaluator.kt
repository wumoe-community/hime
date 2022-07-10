package org.hime.core

import org.hime.cast
import org.hime.parse.*

/**
 * 求值器
 * @param ast    抽象语法树
 * @param symbol 符号表
 * @return       求值返回的值
 */
fun eval(ast: ASTNode, symbol: SymbolTable): Token {
    var temp = ast.tok
    while (true)
        temp = if (temp.type == Type.ID && symbol.contains(cast<String>(temp.value)))
            symbol.get(cast<String>(temp.value))
        else
            break
    ast.tok = temp
    if (ast.isEmpty() && ast.type != AstType.FUNCTION)
        return ast.tok
    // 如果为特殊过程
    if (ast.tok.type == Type.STATIC_FUNCTION) {
        ast.tok = cast<Hime_StaticFunction>(ast.tok.value)(ast, symbol)
        ast.clear()
        return ast.tok
    }
    for (i in 0 until ast.size())
        ast[i].tok = eval(ast[i].copy(), symbol.createChild())
    val args = ArrayList<Token>()
    for (i in 0 until ast.size())
        args.add(ast[i].tok)
    // 如果为内置函数
    if (ast.tok.type == Type.FUNCTION) {
        ast.tok = cast<Hime_Function>(ast.tok.value)(args, symbol)
        ast.clear()
        return ast.tok
    }
    // 如果为自举函数
    if (ast.tok.type == Type.HIME_FUNCTION) {
        ast.tok = cast<Hime_HimeFunction>(ast.tok.value)(args)
        ast.clear()
        return ast.tok
    }
    return ast.tok
}
