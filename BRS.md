# Docstring Freshness Monitor — Business Requirements Specification (BRS)

**Verze:** 2026.2.1
**Autor:** Jakub Jirák
**Datum:** 12. února 2026
**Model:** Freemium JetBrains Plugin

---

## 1. Executive Summary

Docstring Freshness Monitor řeší univerzální problém — dokumentace v kódu zastarává, aniž by si toho někdo všiml. Plugin přináší „freshness skóre" pro každý docstring, čímž vytváří návykovou metriku kvality dokumentace. Blue ocean produkt bez přímé konkurence.

---

## 2. Obchodní cíle

### 2.1 Primární cíle
1. **Návyková metrika** — jednou vidíte freshness skóre, nemůžete ho ignorovat
2. **Code Health Suite brand** — společný brand s Dead Code & Drift Radar
3. **Enterprise appeal** — documentation quality metrics pro compliance/audit

### 2.2 KPI
| Metrika | Cíl (6 měsíců) | Cíl (12 měsíců) |
|---------|:---:|:---:|
| Celkové instalace | 8 000 | 30 000 |
| MAU | 3 000 | 12 000 |
| Paid konverze | 4 % | 6 % |
| Marketplace rating | ≥ 4.0 | ≥ 4.3 |

---

## 3. Cílový trh

### 3.1 Primární segmenty
- **Vývojáři v týmech 10+** (50 %) — dokumentace se rozpadá se škálou
- **Open source maintaineři** (20 %) — dokumentace = onboarding kvalita
- **QA / Tech leads** (30 %) — metriky kvality kódu

### 3.2 Velikost trhu
- JetBrains IDE uživatelé: ~15M
- Pracují s docstringy: ~70 % = ~10.5M
- Cílovka (doc-conscious): ~3M
- Adresovatelný trh: ~750k

---

## 4. Konkurenční prostředí

### 4.1 Přímá konkurence
**Neexistuje.** Všechny doc pluginy generují nebo kontrolují existenci, nikdo neměří aktuálnost.

### 4.2 Nepřímá konkurence
| Řešení | Omezení |
|--------|---------|
| Trelent / OkDoc (AI generátory) | Generují, neměří freshness |
| SonarQube doc rules | Jen existence, ne aktuálnost |
| Code review manuálně | Subjektivní, neškálovatelné |

---

## 5. Revenue model

### 5.1 Cenová strategie
| Tier | Cena | Obsah |
|------|------|-------|
| **Free** | $0 | Freshness skóre, gutter ikony, 1 jazyk |
| **Pro** | $2.90/měsíc nebo $29/rok | Všechny jazyky, dashboard, CI, export |
| **Team** | $5.90/user/měsíc | Pro + team metriky, compliance reports |

### 5.2 Projekce příjmů
| Období | Instalace | Paid (5%) | MRR |
|--------|:---------:|:---------:|:---:|
| M6 | 8 000 | 400 | $1 160 |
| M12 | 30 000 | 1 500 | $4 350 |
| M18 | 60 000 | 3 000 | $8 700 |

---

## 6. Go-to-Market strategie

### 6.1 Launch
1. Blog post: „Your Documentation Is Lying to You — Here's How to Know"
2. Reddit: r/programming, r/java, r/python
3. Cross-promo s Dead Code & Drift Radar (Code Health Suite)

### 6.2 Growth
- **Virální metrika** — „Your project documentation freshness: 62 %" share badge
- **CI integrace** — CI failure = team adoption driver
- **Conference** — „Documentation Decay: Measuring the Unmeasurable"

---

## 7. Rizika a mitigace

| Riziko | Pravděpodobnost | Dopad | Mitigace |
|--------|:---:|:---:|---------|
| False positives (refaktoring bez doc změny) | Vysoká | Střední | „Mark as reviewed" + smart heuristiky |
| Git blame pomalý na velkých repo | Střední | Střední | Caching, inkrementální kalkulace |
| Nízká perceived value (jen metriky) | Střední | Vysoký | Actionable suggestions, CI integrace |
| AI doc generátory sníží potřebu | Nízká | Střední | Freshness validuje i AI-generated docs |

---

## 8. Milníky

| Milestone | Popis |
|-----------|-------|
| **M1: MVP** | Java/Kotlin freshness, gutter ikony, inspection |
| **M2: Multi-lang** | Python, JS/TS podpora |
| **M3: Dashboard** | Projekt-wide přehled, treemap |
| **M4: CI** | CLI runner, SARIF output |
| **M5: Suite** | Code Health Suite integrace s Dead Code Radar |

---

## 9. Success Criteria

1. 15 000+ instalací do 9 měsíců
2. Marketplace rating ≥ 4.0
3. ≥ 4 % konverze na paid tier
4. Alespoň 1 enterprise zákazník (team tier)
5. Code Health Suite brand recognition
