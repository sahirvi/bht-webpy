import math


def flaeche_berechnen (radius):
    a = math.pi * radius**2
    print ('Radius: ', radius)
    print('Flächeninhalt: ', a)
    return a

def umfang_berechnen (radius):
    u = 2 * math.pi * radius
    print('Radius: ', radius)
    print('Umfang: ', u)
    return u

flaeche_berechnen(3)
umfang_berechnen(3)