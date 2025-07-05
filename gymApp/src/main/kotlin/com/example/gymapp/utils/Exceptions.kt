package com.example.gymapp.utils

class IncorrectVoucherException(): RuntimeException("Este voucher no es para esta actividad")
class NonOwnVoucherException(): RuntimeException("No puede usar este voucher")
class NoRemainingClassesException(activity: String): RuntimeException("No hay voucher válido para la actividad $activity")
class TurnAlreadyFullException(): RuntimeException("No puedes reservar porque el turno ya está lleno")
class NoTurnsForActivityException(): RuntimeException("No hay turnos para esta actividad, prueba mas tarde")
class UsernameAlreadyTakenException(val username: String) : RuntimeException("El usuario '$username' ya está registrado")
class MemberAlreadyRegisteredException() : RuntimeException("Ya estas registrado en este turno")
class MemberNotRegisteredInTurnException() : RuntimeException("No estas registrado en este turno")
class PassedTimeOnTurnException() : RuntimeException("Ya pasó el tiempo límite para la cancelación del turno")
class NonActiveBodyBuildingSubscriptionException(val username: String): RuntimeException("$username no tenes una suscripcion activa")
class HaveAlreadyEntryBodyBuildingException(val username: String): RuntimeException("$username Ya registraste un ingreso hoy")
class NoDaysLeftInBodyBuildingSubscriptionException(val username: String): RuntimeException("$username ya ingresaste todos los dias correspondientes a tu suscripcion")