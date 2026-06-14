# Instrukcja grafiki w Blockbench

Ten mod ma obecnie proste placeholdery voxelowe. Grafik moze je zastapic modelami z Blockbench bez dotykania logiki moda, jesli zachowa nazwy plikow i sciezki zasobow.

## Ustawienia projektu

1. W Blockbench wybierz `File > New > Java Block/Item`.
2. Ustaw teksture na 16 x 16 albo 32 x 32 px. Styl powinien zostac Minecraftowy, czytelny i niskopoligonowy.
3. Eksportuj jako `File > Export > Export Block/Item Model`.
4. Modele blokow zapisuj do `src/main/resources/assets/gutenbergpress/models/block/`.
5. Modele itemow zapisuj do `src/main/resources/assets/gutenbergpress/models/item/`.
6. Tekstury blokow zapisuj do `src/main/resources/assets/gutenbergpress/textures/block/`.
7. Tekstury itemow zapisuj do `src/main/resources/assets/gutenbergpress/textures/item/`.

Nie zmieniaj namespace `gutenbergpress`. Wszystkie referencje tekstur w JSON powinny wygladac tak:

```json
"textures": {
  "all": "gutenbergpress:block/wooden_frame"
}
```

albo dla itemow:

```json
"textures": {
  "layer0": "gutenbergpress:item/printing_ink"
}
```

## Najwazniejsze modele prasy

Te pliki odpowiadaja za widoczne czesci wieloblokowej prasy:

| Czesc w grze | Model JSON | Tekstura |
| --- | --- | --- |
| Glowny blok prasy | `models/block/gutenberg_press_master.json` | `textures/block/press_master.png` |
| Slupy ramy | `models/block/wooden_frame_part.json` | `textures/block/wooden_frame.png` |
| Dolne belki | `models/block/base_beam.json` | `textures/block/base_beam.png` |
| Gorne belki | `models/block/top_beam.json` | `textures/block/top_beam.png` |
| Loze drukarskie | `models/block/printing_bed.json` | `textures/block/printing_bed.png` |
| Szuflada wysunieta | `models/block/printing_drawer.json` | `textures/block/printing_drawer.png` |
| Szuflada wsunieta | `models/block/printing_drawer_inserted.json` | `textures/block/printing_drawer_inserted.png` |
| Czyste czcionki | `models/block/type_clean.json` | `textures/block/type_clean.png` |
| Nafarbowane czcionki | `models/block/type_inked.json` | `textures/block/type_inked.png` |
| Czysty papier | `models/block/paper_blank.json` | `textures/block/paper_blank.png` |
| Zadrukowany papier | `models/block/paper_printed.json` | `textures/block/paper_printed.png` |
| Plyta dociskowa wysoko | `models/block/platen.json` | `textures/block/platen.png` |
| Plyta dociskowa nisko | `models/block/platen_lowered.json` | `textures/block/platen_lowered.png` |
| Sruba przed dokreceniem | `models/block/press_screw.json` | `textures/block/press_screw.png` |
| Sruba po dokreceniu | `models/block/press_screw_turned.json` | `textures/block/press_screw_turned.png` |
| Uchwyt sruby | `models/block/screw_handle.json` | `textures/block/screw_handle.png` |
| Uchwyt sruby obrocony | `models/block/screw_handle_turned.json` | `textures/block/screw_handle_turned.png` |

`models/block/empty.json` jest celowo niewidoczny. Nie dodawaj do niego geometrii.

## Modele itemow

Itemy maja osobne modele i tekstury:

| Item | Model JSON | Tekstura |
| --- | --- | --- |
| Prasa Gutenberga | `models/item/gutenberg_press.json` | uzywa modelu bloku |
| Szuflada drukarska | `models/item/printing_drawer.json` | uzywa modelu bloku |
| Sruba prasy | `models/item/press_screw.json` | uzywa modelu bloku |
| Element ramy | `models/item/wooden_frame_part.json` | uzywa modelu bloku |
| Uklad ruchomych czcionek | `models/item/movable_type_composition.json` | `textures/item/movable_type_composition.png` |
| Walek z farba | `models/item/ink_roller.json` | `textures/item/ink_roller.png` |
| Farba drukarska | `models/item/printing_ink.json` | `textures/item/printing_ink.png` |
| Czysty arkusz | `models/item/blank_paper_sheet.json` | `textures/item/blank_paper_sheet.png` |
| Zadrukowany arkusz | `models/item/printed_paper_sheet.json` | `textures/item/printed_paper_sheet.png` |

## Zasady techniczne

- Nie zmieniaj nazw stanow w `src/main/resources/assets/gutenbergpress/blockstates/press_part.json`, na przyklad `drawer_out`, `type_inked`, `paper_printed`, `platen_low`. Te nazwy sa polaczone z enumem `PressPart` w kodzie.
- Mozesz zmieniac geometrie i tekstury modeli pod warunkiem, ze nazwy plikow zostaja takie same.
- Jeden model bloku powinien miescic sie w standardowej przestrzeni 16 x 16 x 16. Wielkosc calej prasy powstaje z wielu blokow, nie z jednego ogromnego modelu.
- Nie uzywaj realistycznych high-poly modeli ani importow OBJ jako glownego assetu. Ten mod ma wygladac voxelowo i czytelnie w stylu Minecrafta.
- Dla ruchu mechaniki przygotuj osobne warianty wizualne zamiast animacji: `printing_drawer.json` kontra `printing_drawer_inserted.json`, `platen.json` kontra `platen_lowered.json`, `press_screw.json` kontra `press_screw_turned.json`.
- Tekstury zapisz jako PNG. Najbezpieczniej uzyc 16 x 16 lub 32 x 32 px.

## Sugestie artystyczne

- Rama: ciemne drewno renesansowe, grube belki, wyrazne slupy.
- Szuflada: deski z widoczna prowadnica.
- Czcionki: metalowe prostokaty z wypuklym wzorem liter.
- Nafarbowane czcionki: ta sama geometria, ale ciemna powierzchnia tuszu.
- Papier: jasny pergamin; wersja `paper_printed` powinna miec czarne znaki.
- Sruba: drewniany lub metalowo-drewniany pionowy gwint, widoczny uchwyt poziomy.
- Plyta dociskowa: masywna, plaska, ciemniejsza niz papier i loze.

## Test po eksporcie

Po podmianie modeli i tekstur uruchom:

```sh
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-25.jdk/Contents/Home PATH=/Library/Java/JavaVirtualMachines/jdk-25.jdk/Contents/Home/bin:$PATH ./gradlew build
```

Dla kontroli wizualnej najlepiej uruchomic klienta:

```sh
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-25.jdk/Contents/Home PATH=/Library/Java/JavaVirtualMachines/jdk-25.jdk/Contents/Home/bin:$PATH ./gradlew runClient
```

W grze sprawdz pelny cykl: wstaw czcionki, naloz farbe, poloz papier, wsun szuflade, dokrec srube, wykonaj odbicie i wyjmij zadrukowany arkusz.
