import re

# 검색 키워드
SEARCH_KEYWORD = ["N.Y.Sup"] # WestLaw
SKIP_KEYWORD = ["Appellate", "second trial", "3rd trial", "Second Department", "Third Department"]
HEADERS = {'User-Agent' : 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36'}

# reulst 키워드
PLAINTIFF_FAVOR_KEYWORD = [
    re.compile("Judgement for the plaintiff", re.I), re.compile("Liability establish", re.I), re.compile("Awarded damage", re.I), 
    re.compile("Verdict for the plaintiff", re.I), re.compile("Breach of duty", re.I), re.compile("Proven by", re.I), re.compile("Defendant pay to the Plaintiff", re.I), 
    re.compile("Granting the motion", re.I), re.compile("Injunction granted", re.I),re.compile("Guilty", re.I), re.compile("Injunction", re.I),
    re.compile("Conviction", re.I), re.compile("Sentence", re.I), re.compile("Verdict for the prosecution", re.I), re.compile("Plea bargain", re.I), 
    re.compile("Beyond a reasonable doubt", re.I),re.compile("Breach of Contract", re.I), re.compile("Breach of fiduciary duty", re.I), 
    re.compile("Specific Performance", re.I), re.compile("Compensatory damage", re.I), re.compile("Punitive damage", re.I), re.compile("Remedy", re.I),
    re.compile("Defendant is ORDERED", re.I), re.compile("ORDERED that Defendant", re.I), re.compile("Defendant shall pay", re.I),
    re.compile("Respondent is ORDERED", re.I), re.compile("ORDERED that Respondent", re.I), re.compile("Respondent shall pay", re.I),
]   

DEPANDENT_FAVOR_KEYWORD = [
    re.compile("Judgement for the defendant", re.I), re.compile("Not Liable", re.I), re.compile("Case Dismiss", re.I), 
    re.compile("No cause of action", re.I), re.compile("Judgement Granted for defendant", re.I), re.compile("Plaintiff is ORDERED", re.I), 
    re.compile("Denied Motion",re.I), re.compile("Failure to Prove", re.I), re.compile("No damages award", re.I), 
    re.compile("Not Guilty", re.I), re.compile("Acquittal", re.I), re.compile("Verdict for the defendant", re.I), 
    re.compile("Exoneration", re.I), re.compile("Reasonable Doubt", re.I), re.compile("Mistrial", re.I), 
    re.compile("No breach found", re.I), re.compile("Dismissal of claim", re.I), re.compile("Plaintiff pay to the Defendant", re.I), 
    re.compile("Counterclaim successful", re.I), re.compile("Defendants are entitled", re.I), re.compile("Respondents are entitled", re.I), 
    re.compile("Plaintiff shall pay", re.I), re.compile("Petitioner shall pay", re.I), re.compile("ORDERED that Petitioner", re.I),
    re.compile("Plaintiff has not proven", re.I), re.compile("Plaintiff lacks", re.I), re.compile("ORDERED that Plaintiff", re.I),
    re.compile("Petitioner has not proven", re.I), re.compile("Petitioner lacks", re.I), 
]

# 변호사 검색용 키워드
ATTORNEY_KEYWORD = ["petitioner", "plaintiff", "respondent", "defendant"] 

# 변호인 키워드
BEFORE_ATTORNEY_COMPILE = [
    re.compile("Plaintiff(\'?\w?\s?)*\.",re.I),
    re.compile("Petitioner(\'?\w?\s?)*\.",re.I),
    re.compile("Defendant(\'?\w?\s?)*\.",re.I),
    re.compile("Respondent(\'?\w?\s?)*\.",re.I)
] 

AFTER_ATTORNEY_COMPILE = [
    re.compile("Plaintiff'?\w?",re.I),
    re.compile("Petitioner'?\w?",re.I),
    re.compile("Defendant'?\w?",re.I),
    re.compile("Respondent'?\w?",re.I)
]

# 제거 키워드
REMOVE_KEYWORD = ["we", ",", "Esq", "Esq.", "ESQ", "ESQ." "(", ")", "P.C.", " .", "Plaintiff", "Petitioner", "Defendant", "Respondent", "Appellant" "appellant", "respondent", "plaintiff", "petitional", "defendent"]

LOC = [
    "New York",
    "N.Y.",
    "NY",
    "Brooklyn"
]

LAW_FIRMS = [
    "Pollack Pollack Isaac & Decicco",
    "Fabiani Cohen & Hall LLP",
    "Nicoletti Hornig & Sweeney",
    "Bartholow & Miller",
    "Akiva Shapiro Firm",
    "Ainsworth Gorkin PLLC",
    "Cramer & Summit",
    "Lieb at Law",
    "Hegge & Confusione",
    "Lipsig, Shapey, Manus & Moverman, P.C.",
    "Gannon, Rosenfarb & Drossman",
    "AWN&R Commercial Law Group",
    "Central Islip",
    "Woods Oviatt Gilman LLP",
    "Skadden, Arps, Slate, Meagher & Flom LLP",
    "Latham & Watkins LLP",
    "Baker McKenzie",
    "Sidley Austin LLP",
    "Hogan Lovells",
    "Clifford Chance LLP",
    "White & Case LLP",
    "Jones Day",
    "Kirkland & Ellis LLP",
    "Simpson Thacher & Bartlett LLP",
    "Davis Polk & Wardwell LLP",
    "Paul, Weiss, Rifkind, Wharton & Garrison LLP",
    "Morgan, Lewis & Bockius LLP",
    "Mayer Brown LLP",
    "Cleary Gottlieb Steen & Hamilton LLP",
    "Ropes & Gray LLP",
    "WilmerHale",
    "Weil, Gotshal & Manges LLP",
    "Arnold & Porter",
    "Shearman & Sterling LLP",
    "Dechert LLP",
    "O'Melveny & Myers LLP",
    "Freshfields Bruckhaus Deringer LLP",
    "Reed Smith LLP",
    "Gibson, Dunn & Crutcher LLP",
    "Proskauer Rose LLP",
    "Baker Botts LLP",
    "Hunton Andrews Kurth LLP",
    "Akin Gump Strauss Hauer & Feld LLP",
    "Quinn Emanuel Urquhart & Sullivan LLP",
    "Covington & Burling LLP",
    "Greenberg Traurig, LLP",
    "Sullivan & Cromwell LLP",
    "Dentons",
    "Perkins Coie LLP",
    "Ballard Spahr LLP",
    "McDermott Will & Emery LLP",
    "Winston & Strawn LLP",
    "Paul Hastings LLP",
    "Steptoe & Johnson LLP",
    "Nelson Mullins Riley & Scarborough LLP",
    "Morrison & Foerster LLP",
    "Vinson & Elkins LLP",
    "Husch Blackwell LLP",
    "Holland & Knight LLP",
    "Seyfarth Shaw LLP",
    "Lewis Brisbois Bisgaard & Smith LLP",
    "Schulte Roth & Zabel LLP",
    "Foley & Lardner LLP",
    "Orrick, Herrington & Sutcliffe LLP",
    "DLA Piper",
    "Eversheds Sutherland",
    "Bryan Cave Leighton Paisner LLP",
    "K&L Gates LLP",
    "Blank Rome LLP",
    "Alston & Bird LLP",
    "Davis Wright Tremaine LLP",
    "McGuireWoods LLP",
    "Jackson Lewis P.C.",
    "Squire Patton Boggs LLP",
    "Littler Mendelson P.C.",
    "Mintz Levin Cohn Ferris Glovsky and Popeo P.C.",
    "Arnold & Porter Kaye Scholer LLP",
    "Zuckerman Spaeder LLP",
    "Richards, Layton & Finger, P.A.",
    "Bingham Greenebaum Doll LLP",
    "Hogan Lovells",
    "Dorsey & Whitney LLP",
    "Akerman LLP",
    "Carlton Fields",
    "Williams & Connolly LLP",
    "Kramer Levin Naftalis & Frankel LLP",
    "Day Pitney LLP",
    "Stradley Ronon Stevens & Young LLP",
    "Munger, Tolles & Olson LLP",
    "Venable LLP",
    "Wilmer Cutler Pickering Hale and Dorr LLP",
    "Kilpatrick Townsend & Stockton LLP",
    "Coblentz Patch Duffy & Bass LLP",
    "Farella Braun + Martel LLP",
    "Cooley LLP",
    "Morgan Lewis & Bockius LLP",
    "Riker Danzig Scherer Hyland & Perretti LLP",
    "Stinson LLP",
    "Gardere Wynne Sewell LLP",
    "Schwabe, Williamson & Wyatt",
    "Barnes & Thornburg LLP",
    "Quarles & Brady LLP",
    "Madsen, Goldman & Holm LLP",
    "Davis Polk & Wardwell LLP",
    "Crowell & Moring LLP",
    "Pillsbury Winthrop Shaw Pittman LLP",
    "McCarter & English LLP",
    "Lewis Brisbois",
    "Foley & Lardner LLP",
    "Stradley Ronon Stevens & Young LLP",
    "Lowenstein Sandler LLP",
]