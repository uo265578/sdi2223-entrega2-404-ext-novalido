const{check} = require('express-validator');
const userRepository = require("../repositories/usersRepository");
const express = require("express");
let crypto = require('crypto');
exports.userValidatorSignup = [
    check('email', 'El email no debe ser vacio').trim().not().isEmpty(),
    check('name', 'El nombre no debe ser vacio').trim().not().isEmpty(),
    check('surname', 'El apellido no debe ser vacio').trim().not().isEmpty(),
    check('password', 'La contrasena no debe ser vacia').trim().not().isEmpty(),
    check('passwordConfirm', 'La repeticion de contrasena no debe ser vacia').trim().not().isEmpty(),
    check('email',"El email no es valido").isEmail(),
    check('date').custom((value) => {
        if (!isValidDate(value)) {
            throw new Error('La fecha no es valida');
        }
        return true;
    }),
    check('email').custom(async (value) => {
        let filter ={email:value}
        let user = await userRepository.findUser(filter,{});
        if(user!==null){
            throw new Error("Ese email ya existe");
        }
        return true;
    }),
    check('password').custom(async (value, { req }) => {
        if(value!==req.body.passwordConfirm){
            throw new Error("Las contrasenas no coinciden");
        }
    })
]

exports.userValidatorLogIn = [
    check('email', 'El email no debe ser vacio').trim().not().isEmpty(),
    check('password', 'La contrasena no debe ser vacia').trim().not().isEmpty(),
    check('email',"El email no es valido").isEmail(),
    check('email').custom(async (value, { req }) => {
        let securePassword = crypto.createHmac('sha256', "abcdefg").update(req.body.password).digest('hex');
        let filter = {email: value, password: securePassword}
        let user = await userRepository.findUser(filter, {})
        if(user===null){
            throw new Error("No se encuentra el usuario");
        }
    })
]

exports.userValidatorDelete = [
    check("id","No puedes eliminar al usuario administrador").not().equals("admin")
]

//Comprueba que una fecha en el formato yyyy-mm-dd es válida
function isValidDate(date){
    let checkDate =Date.parse(date);
    if(isNaN(checkDate)){ //La fecha no existe
        return false;
    }else{
        let current = new Date();
        if(checkDate> Number(current)){ //La fecha es superior al dia actual
            return false;
        }
        let date= new Date(0);
        date.setUTCSeconds(checkDate);
        let age = new Date(current - checkDate).getFullYear() - 1970;
        if(age>120){ //La persona debe tener una edad posible
            return false;
        }
    }
    return true;
}


