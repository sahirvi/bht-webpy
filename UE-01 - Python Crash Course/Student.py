class Student(object):

    def __init__(self, vorname, nachname, matrikelnummer, geburtsdatum):
        self.vorname = vorname
        self.nachname = nachname
        self.matrikelnummer = matrikelnummer
        self.geburtsdatum = geburtsdatum
        self.table = {}

    def note_eintragen (self, kurs, note):
        self.table.update({kurs:note})

    def noten_berichten(self):
       print(self.table)

    def noten_schummeln(self):
        notenanzahl = 0
        for value in self.table:
            if self.table[value] != 1.0:
                self.table[value] = 1.0
                notenanzahl += 1
        return notenanzahl

    def __str__(self):
        return f"Name des Studenten: {self.vorname} {self.nachname}, Matrikelnummer: {self.matrikelnummer} und Geburtsdatum: {self.geburtsdatum}"

    def __repr__(self):
        return f"Student('{self.vorname}', '{self.nachname}', {self.matrikelnummer}, '{self.geburtsdatum}')"


Sahiram = Student('Sahiram', 'Ravikumar', 899517, '24.11.1997')
Sahiram.note_eintragen('Programmierung I', 2.7)
Sahiram.note_eintragen('Programmierung II', 1.7)
Sahiram.note_eintragen('Mathematik I', 2.0)
Sahiram.note_eintragen('Mathematik II', 2.3)
Sahiram.note_eintragen('Software Engineering I', 1.0)

print(str(Sahiram))
print(repr(Sahiram))

Sahiram.noten_berichten()
print("Anzahl der geänderten Noten: ", Sahiram.noten_schummeln())
Sahiram.noten_berichten()