from gliner import GLiNER

class NLE:
    def __init__(self):
        self.model = GLiNER.from_pretrained("urchade/gliner_mediumv2.1")
    
    def predict(self, text, labels):
        entities = self.model.predict_entities(text, labels)
        ret = []
        for entity in entities:
            ret.append(entity["text"])
        return ret
