export interface Generator {
  id: number;
  model: string;
  name: string;
  capacity?: string;
  locationName?: string;
  ownerEmail?: string;
  latitude?: number;
  longitude?: number;
  note?: string;
  createdAt: string;
}

export interface CreateGeneratorRequest {
  model: string;
  name: string;
  capacity?: string;
  locationName?: string;
  ownerEmail?: string;
  latitude?: number;
  longitude?: number;
  note?: string;
}
