package com.example.gymapp.utils

class IncorrectVoucherException(): RuntimeException("Este voucher no es para esta actividad")
class NonOwnVoucherException(): RuntimeException("No puede usar este voucher")
class NoRemainingClassesException(): RuntimeException("Has utilizado todas las clases de tu voucher")
class TurnAlreadyFullException(): RuntimeException("No puedes reservar porque el turno ya está lleno")
class NoTurnsForActivityException(): RuntimeException("No hay turnos para esta actividad, prueba mas tarde")