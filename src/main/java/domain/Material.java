package domain;

public class Material {
    
    private int zona;
    private int sku;
    private double precio;

    public Material(int zona, int sku, double precio) {
        this.zona = zona;
        this.sku = sku;
        this.precio = precio;
    }

    public Material(int sku, double precio) {
        this.sku = sku;
        this.precio = precio;
    }
    

    public int getZona() {
        return zona;
    }

    public int getSku() {
        return sku;
    }

    public double getPrecio() {
        return precio;
    }
    
    
    public void setPrecio(double precio) {
		this.precio = precio;
	}

	@Override
    public String toString() {
        return  sku + "," + precio;
    }
    
    
}
