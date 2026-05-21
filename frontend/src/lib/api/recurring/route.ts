import { cookies } from 'next/headers';
import { NextResponse } from 'next/server';

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_URL ||
  'http://localhost:8080';

export async function POST(request: Request) {
  try {
    const body = await request.json();

    const cookieStore = await cookies();

    const token =
      cookieStore.get('accessToken')?.value;

    if (!token) {
      return NextResponse.json(
        { error: 'UNAUTHORIZED' },
        { status: 401 }
      );
    }

    const response = await fetch(
      `${API_BASE_URL}/recurring`,
      {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(body),
      }
    );

    const data = await response.json();

    return NextResponse.json(data, {
      status: response.status,
    });
  } catch (error) {
    console.error(error);

    return NextResponse.json(
      { error: 'INTERNAL_ERROR' },
      { status: 500 }
    );
  }
}